package com.example.channapatnatoys.data.repository

import android.util.Log
import com.example.channapatnatoys.data.model.Toy
import com.example.channapatnatoys.data.remote.FirebaseService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToyRepository @Inject constructor(
    private val firebaseService: FirebaseService,
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val TAG = "ToyRepository"
    }

    // getToyById stays as flow{} — one shot fetch is correct here
    // because verify screen fetches a single toy once per ID entry
    fun getToyById(id: String): Flow<Toy?> = flow {
        emit(firebaseService.getToyById(id))
    }.flowOn(Dispatchers.IO)

    // getAllToys uses callbackFlow — real-time updates for catalog screen
    // if you add a new toy to Firestore, catalog updates automatically
    fun getAllToys(): Flow<List<Toy>> = callbackFlow {

        Log.d(TAG, "Starting Firestore listener for toys...")

        val listener = firestore.collection("toys")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    Log.e(TAG, "Firestore error: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot == null || snapshot.isEmpty) {
                    Log.w(TAG, "Toys snapshot empty")
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val toys = snapshot.documents.mapNotNull { doc ->
                    Log.d("RepoDebug", "Doc ${doc.id} imageUrl: '${doc.getString("imageUrl")}'")
                    try {
                        Toy(
                            id              = doc.id,
                            name            = doc.getString("name").orEmpty(),
                            artisanName     = doc.getString("artisanName").orEmpty(),
                            workshopAddress = doc.getString("workshopAddress").orEmpty(),
                            lacColor        = doc.getString("lacColor").orEmpty(),
                            madeYear        = doc.getLong("madeYear")?.toInt() ?: 0,
                            imageUrl        = doc.getString("imageUrl").orEmpty(),
                            category        = doc.getString("category").orEmpty()
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Parse error for doc ${doc.id}: ${e.message}")
                        null
                    }
                }

                Log.d(TAG, "Loaded ${toys.size} toys from Firestore")
                trySend(toys)
            }

        awaitClose {
            Log.d(TAG, "Removing toys Firestore listener")
            listener.remove()
        }

    }.catch { e ->
        Log.e(TAG, "Unexpected flow error: ${e.message}", e)
        emit(emptyList())
    }
}