package com.example.channapatnatoys.data.repository

import android.util.Log
import com.example.channapatnatoys.data.model.WorkshopLocation
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkshopRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val TAG = "WorkshopRepository"

        private val FALLBACK_WORKSHOPS = listOf(
            WorkshopLocation(
                id = "1",
                name = "Channapatna Crafts Park",
                address = "Toy Town Main Road, Channapatna",
                lat = 12.6518,
                lng = 77.2089,
                distanceKm = null
            ),
            WorkshopLocation(
                id = "2",
                name = "Organic Lacquer Co-op",
                address = "MG Road, Channapatna",
                lat = 12.6480,
                lng = 77.2020,
                distanceKm = null
            ),
            WorkshopLocation(
                id = "3",
                name = "Heritage Woodworks",
                address = "Kala Nagar, Channapatna",
                lat = 12.6550,
                lng = 77.2100,
                distanceKm = null
            ),
            WorkshopLocation(
                id = "4",
                name = "Lakshmi Craft Studio",
                address = "Heritage Lane, Channapatna",
                lat = 12.6560,
                lng = 77.2055,
                distanceKm = null
            ),
            WorkshopLocation(
                id = "5",
                name = "Venkat Toy House",
                address = "Artisan Colony, Channapatna",
                lat = 12.6478,
                lng = 77.2110,
                distanceKm = null
            )
        )
    }

    fun getWorkshopLocations(): Flow<List<WorkshopLocation>> = callbackFlow {

        Log.d(TAG, "Starting Firestore listener for workshops...")

        val listener = firestore.collection("workshop_locations")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    Log.e(TAG, "Firestore error: ${error.message}")
                    trySend(FALLBACK_WORKSHOPS)
                    return@addSnapshotListener
                }

                if (snapshot == null || snapshot.isEmpty) {
                    Log.w(TAG, "Firestore snapshot empty — using fallback")
                    trySend(FALLBACK_WORKSHOPS)
                    return@addSnapshotListener
                }

                val workshops = snapshot.documents.mapNotNull { doc ->
                    try {
                        val lat = doc.getString("lat")?.toDoubleOrNull()
                            ?: doc.getDouble("lat")
                            ?: 0.0
                        val lng = doc.getString("lng")?.toDoubleOrNull()
                            ?: doc.getDouble("lng")
                            ?: 0.0

                        WorkshopLocation(
                            id         = doc.id,
                            name       = doc.getString("name")    ?: "",
                            address    = doc.getString("address") ?: "",
                            lat        = lat,
                            lng        = lng,
                            distanceKm = null
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to parse doc ${doc.id}: ${e.message}")
                        null
                    }
                }

                Log.d(TAG, "Loaded ${workshops.size} workshops from Firestore")

                val result = workshops.ifEmpty { FALLBACK_WORKSHOPS }
                trySend(result)
            }

        awaitClose {
            Log.d(TAG, "Removing Firestore listener")
            listener.remove()
        }

    }.catch { e ->
        Log.e(TAG, "Unexpected flow error: ${e.message}", e)
        emit(FALLBACK_WORKSHOPS)
    }
}