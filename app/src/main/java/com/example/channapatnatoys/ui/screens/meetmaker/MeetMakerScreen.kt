package com.example.channapatnatoys.ui.screens.meetmaker

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.channapatnatoys.data.model.WorkshopLocation
import com.example.channapatnatoys.ui.UiState
import com.example.channapatnatoys.ui.components.WorkshopCard
import com.example.channapatnatoys.ui.theme.DarkBg
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MeetMakerScreen(
    viewModel: MeetMakerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedWorkshop by remember { mutableStateOf<WorkshopLocation?>(null) }
    
    // mapLoaded = true means Google Maps rendered successfully
    // mapLoadFailed = true means we timed out waiting -> show list fallback
    var mapLoaded by remember { mutableStateOf(false) }
    var mapLoadFailed by remember { mutableStateOf(false) }

    // If the map hasn't confirmed it loaded within 10 seconds, fall back to list
    LaunchedEffect(Unit) {
        delay(10000)
        if (!mapLoaded) {
            mapLoadFailed = true
            android.util.Log.e("MapsDebug", "Map failed to load within 10s. Check Logcat for 'Google Maps Android API' errors.")
        }
    }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        if (locationPermissionsState.allPermissionsGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    viewModel.updateUserLocation(it.latitude, it.longitude)
                }
            }
        } else if (!locationPermissionsState.shouldShowRationale) {
            locationPermissionsState.launchMultiplePermissionRequest()
        }
    }

    // Default camera: Channapatna
    val channapatna = LatLng(12.6518, 77.2089)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(channapatna, 12f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is UiState.Loading -> {
                CircularProgressIndicator(
                    color = DarkBg,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is UiState.Error -> {
                Text(
                    text = state.message,
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error
                )
            }
            is UiState.Success -> {
                val workshops = state.data

                if (mapLoadFailed) {
                    // Fallback: show simple list if Maps API key is missing or timed out
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Nearby Workshops",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = DarkBg,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(workshops) { workshop ->
                                WorkshopCard(
                                    workshop = workshop,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                } else {
                    // Normal: Google Map with pins
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(zoomControlsEnabled = false),
                        onMapLoaded = {
                            mapLoaded = true
                            mapLoadFailed = false
                        }
                    ) {
                        workshops.forEach { workshop ->
                            Marker(
                                state = MarkerState(
                                    position = LatLng(workshop.lat, workshop.lng)
                                ),
                                title = workshop.name.ifBlank { "Workshop" },
                                snippet = workshop.address,
                                icon = BitmapDescriptorFactory.defaultMarker(
                                    BitmapDescriptorFactory.HUE_RED
                                ),
                                onClick = {
                                    selectedWorkshop = workshop
                                    true
                                }
                            )
                        }
                    }
                }
            }
            UiState.Idle -> {}
        }

        // Bottom Sheet for Workshop Info when a pin is tapped
        selectedWorkshop?.let { workshop ->
            ModalBottomSheet(
                onDismissRequest = { selectedWorkshop = null }
            ) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(bottom = 32.dp)
                ) {
                    WorkshopCard(workshop = workshop)
                }
            }
        }
    }
}
