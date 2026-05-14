package com.example.channapatnatoys.ui.screens.meetmaker

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.channapatnatoys.data.model.WorkshopLocation
import com.example.channapatnatoys.data.repository.WorkshopRepository
import com.example.channapatnatoys.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeetMakerViewModel @Inject constructor(
    private val workshopRepository: WorkshopRepository
) : ViewModel() {

    companion object {
        private const val TAG = "MeetMakerViewModel"
    }

    private val _uiState = MutableStateFlow<UiState<List<WorkshopLocation>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<WorkshopLocation>>> = _uiState.asStateFlow()

    private val _workshops = MutableStateFlow<List<WorkshopLocation>>(emptyList())
    private val _userLocation = MutableStateFlow<Location?>(null)

    init {
        loadWorkshops()
        observeWorkshopsAndLocation()
    }

    // Called from MeetMakerScreen when user grants location permission
    fun updateUserLocation(lat: Double, lng: Double) {
        val location = Location("").apply {
            latitude = lat
            longitude = lng
        }
        Log.d(TAG, "User location updated: $lat, $lng")
        _userLocation.value = location
    }

    private fun loadWorkshops() {
        viewModelScope.launch {
            try {
                workshopRepository.getWorkshopLocations().collect { workshops ->
                    Log.d(TAG, "Received ${workshops.size} workshops")
                    _workshops.value = workshops
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load workshops: ${e.message}", e)
                _uiState.value = UiState.Error("Failed to load workshops: ${e.message}")
            }
        }
    }

    // combine() ensures updateUiState fires exactly once
    // when either _workshops or _userLocation changes
    // eliminates the race condition of two separate collect blocks
    private fun observeWorkshopsAndLocation() {
        viewModelScope.launch {
            combine(_workshops, _userLocation) { workshops, location ->
                Pair(workshops, location)
            }.collect { (workshops, location) ->
                updateUiState(workshops, location)
            }
        }
    }

    private fun updateUiState(
        workshops: List<WorkshopLocation>,
        location: Location?
    ) {
        if (workshops.isEmpty()) {
            Log.w(TAG, "Workshop list is empty")
            _uiState.value = UiState.Error("No workshops found.")
            return
        }

        val processedWorkshops = if (location != null) {
            // Calculate distance from user to each workshop and sort
            workshops.map { workshop ->
                val workshopLoc = Location("").apply {
                    latitude = workshop.lat
                    longitude = workshop.lng
                }
                val distanceMeters = location.distanceTo(workshopLoc)
                workshop.copy(distanceKm = distanceMeters / 1000f)
            }.sortedBy { it.distanceKm }
        } else {
            // No user location yet — show all workshops, distance stays null
            workshops
        }

        Log.d(TAG, "UI updated with ${processedWorkshops.size} workshops")
        _uiState.value = UiState.Success(processedWorkshops)
    }
}