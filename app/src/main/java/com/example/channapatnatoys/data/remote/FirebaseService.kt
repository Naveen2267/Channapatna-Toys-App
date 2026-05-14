package com.example.channapatnatoys.data.remote

import android.util.Log
import com.example.channapatnatoys.data.model.Toy
import com.example.channapatnatoys.data.model.WorkshopLocation
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val TAG = "FirebaseService"
    }

    suspend fun getToyById(id: String): Toy? {
        return try {
            val document = firestore.collection("toys").document(id).get().await()
            if (document.exists()) {
                parseToy(document.id, document)
            } else {
                Log.w(TAG, "Toy not found for id: $id")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "getToyById error: ${e.message}", e)
            null
        }
    }

    suspend fun getAllToys(): List<Toy> {
        return try {
            val snapshot = firestore.collection("toys").get().await()
            snapshot.documents.mapNotNull { doc ->
                parseToy(doc.id, doc)
            }
        } catch (e: Exception) {
            Log.e(TAG, "getAllToys error: ${e.message}", e)
            emptyList()
        }
    }

    // FIX: parse manually instead of .toObject()
    // because Firestore stores numbers as Long
    // but our data class uses Int for madeYear
    // .toObject() silently fails on this type mismatch
    private fun parseToy(
        id: String,
        doc: com.google.firebase.firestore.DocumentSnapshot
    ): Toy? {
        return try {
            Toy(
                id             = id,
                name           = doc.getString("name").orEmpty(),
                artisanName    = doc.getString("artisanName").orEmpty(),
                workshopAddress= doc.getString("workshopAddress").orEmpty(),
                lacColor       = doc.getString("lacColor").orEmpty(),
                // FIX: Firestore stores as Long — convert to Int safely
                madeYear       = doc.getLong("madeYear")?.toInt() ?: 0,
                imageUrl       = doc.getString("imageUrl").orEmpty(),
                category       = doc.getString("category").orEmpty()
            )
        } catch (e: Exception) {
            Log.e(TAG, "parseToy error for doc $id: ${e.message}", e)
            null
        }
    }

    // Removed the redundant useless .map{} at the end
    suspend fun getWorkshopLocations(): List<WorkshopLocation> {
        return try {
            val snapshot = firestore.collection("workshop_locations").get().await()
            snapshot.documents.mapNotNull { doc ->
                try {
                    WorkshopLocation(
                        id      = doc.id,
                        name    = doc.getString("name").orEmpty(),
                        address = doc.getString("address").orEmpty(),
                        lat     = doc.getString("lat")?.toDoubleOrNull()
                            ?: doc.getDouble("lat") ?: 0.0,
                        lng     = doc.getString("lng")?.toDoubleOrNull()
                            ?: doc.getDouble("lng") ?: 0.0,
                        distanceKm = null
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "parseWorkshop error for doc ${doc.id}: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getWorkshopLocations error: ${e.message}", e)
            emptyList()
        }
    }
}