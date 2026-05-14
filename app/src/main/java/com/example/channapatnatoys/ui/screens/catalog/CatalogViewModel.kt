package com.example.channapatnatoys.ui.screens.catalog

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.channapatnatoys.data.model.Toy
import com.example.channapatnatoys.data.repository.ToyRepository
import com.example.channapatnatoys.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val toyRepository: ToyRepository
) : ViewModel() {

    companion object {
        private const val TAG = "CatalogViewModel"

        // Fallback with real GitHub raw image URLs
        private val FALLBACK_TOYS = listOf(
            Toy(
                id = "123456",
                name = "Rocking Horse",
                artisanName = "Raju Kumar",
                workshopAddress = "Channapatna Workshop",
                lacColor = "Red, Green",
                madeYear = 2024,
                imageUrl = "https://raw.githubusercontent.com/Naveen2267/channapatna-toys-assets/main/rocking-horse.jpg",
                category = "Rocking Horse"
            ),
            Toy(
                id = "234567",
                name = "Spinning Top",
                artisanName = "Meena Devi",
                workshopAddress = "Channapatna Workshop",
                lacColor = "Yellow, Blue",
                madeYear = 2024,
                imageUrl = "https://raw.githubusercontent.com/Naveen2267/channapatna-toys-assets/main/spinng-toy-3.jpg",
                category = "Top"
            ),
            Toy(
                id = "345678",
                name = "Number Puzzle",
                artisanName = "Syed Ali",
                workshopAddress = "Channapatna Workshop",
                lacColor = "Multi",
                madeYear = 2023,
                imageUrl = "",
                category = "Puzzle"
            ),
            Toy(
                id = "456789",
                name = "Elephant Pull Toy",
                artisanName = "Prakash",
                workshopAddress = "Channapatna Workshop",
                lacColor = "Green, Red",
                madeYear = 2024,
                imageUrl = "",
                category = "Animal"
            ),
            Toy(
                id = "567890",
                name = "Dasara Doll",
                artisanName = "Lakshmi N",
                workshopAddress = "Channapatna Workshop",
                lacColor = "Gold, Red",
                madeYear = 2022,
                imageUrl = "",
                category = "Doll"
            )
        )
    }

    private var allToys: List<Toy> = emptyList()

    private val _uiState = MutableStateFlow<UiState<List<Toy>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Toy>>> = _uiState.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    init {
        loadToys()
    }

    private fun loadToys() {
        viewModelScope.launch {
            try {
                toyRepository.getAllToys().collect { toys ->
                    allToys = if (toys.isNotEmpty()) {
                        Log.d(TAG, "Loaded ${toys.size} toys from Firestore")
                        toys
                    } else {
                        // Firestore empty — use fallback
                        Log.w(TAG, "No toys from Firestore — using fallback")
                        FALLBACK_TOYS
                    }
                    applyFilter(_selectedCategory.value)
                }
            } catch (e: Exception) {
                // Firestore threw — use fallback
                Log.e(TAG, "loadToys error: ${e.message}", e)
                allToys = FALLBACK_TOYS
                applyFilter(_selectedCategory.value)
            }
        }
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
        applyFilter(category)
    }

    private fun applyFilter(category: String) {
        _uiState.value = if (category == "All") {
            UiState.Success(allToys)
        } else {
            UiState.Success(allToys.filter { it.category == category })
        }
    }
}