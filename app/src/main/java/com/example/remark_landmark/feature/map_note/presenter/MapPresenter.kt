package com.example.remark_landmark.feature.map_note.presenter

import android.content.ContentValues
import android.util.Log
import com.example.remark_landmark.feature.map_note.model.MarkerInfoModel
import com.example.remark_landmark.feature.map_note.view.IMapView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

// Presenter class for handling map note features and communicating with the view
class MapPresenter(var iMapView: IMapView) : IMapPresenter {
    // Firebase Firestore instance
    private var db: FirebaseFirestore? = null

    // Initialize the Firestore instance
    private fun useFireStore() {
        db = FirebaseFirestore.getInstance()
    }

    // Search markers in Firestore based on the search value
    override fun searchMarkers(searchValue: String) : List<MarkerInfoModel> {
        useFireStore() // Ensure Firestore instance is initialized
        val documentReference = db!!.collection("notes")
        documentReference.whereEqualTo("note", searchValue)
        // Currently, the method returns an empty list; implementation for fetching data is incomplete
        return ArrayList()
    }

    // Fetch all markers from Firestore and pass them to the view
    override fun fetchMarkers() {
        useFireStore() // Ensure Firestore instance is initialized
        val documentReference = db!!.collection("notes")

        // Set up a snapshot listener to listen for changes in the "notes" collection
        documentReference.addSnapshotListener { snapshot, e ->
            if (e != null) {
                // Log and handle the error if the listener fails
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.documents.isNotEmpty()) {
                // If snapshot is not null and contains documents, process the data
                Log.d(ContentValues.TAG, "Current data: ${snapshot.documents}")
                val markerList: ArrayList<MarkerInfoModel> = ArrayList()

                try {
                    // Loop through each document in the snapshot and extract marker data
                    for (document in snapshot.documents) {
                        Log.d("Marker data", "marker data: ${document.id} ${document.data} $document")
                        val lat = document.data?.get("lat").toString().toDouble()
                        val long = document.data?.get("long").toString().toDouble()
                        val note = document.data?.get("note").toString()
                        val owner = document.data?.get("owner").toString()
                        val marker = MarkerInfoModel(lat = lat, long = long, note = note, owner = owner)
                        markerList.add(marker)
                    }
                } catch (e: Exception) {
                    // Log any exceptions that occur during data processing
                    Log.d("Error in fetch", e.toString())
                }

                // Notify the view that markers have been successfully fetched
                iMapView.onFetchMarkerSuccess(markerList)
            } else {
                // Log if the snapshot is null or contains no documents
                Log.d(ContentValues.TAG, "Current data: null")
            }
        }
    }

    // Create a new marker in Firestore
    override fun createMarker(owner: String, lat: Double, long: Double, note: String) {
        useFireStore() // Ensure Firestore instance is initialized
        // Create a new marker with latitude, longitude, note, and owner information
        val markerNote = hashMapOf(
            "lat" to lat,
            "long" to long,
            "note" to note,
            "owner" to owner,
        )

        // Add the new marker to the "notes" collection
        db?.collection("notes")
            ?.add(markerNote)
            ?.addOnSuccessListener { documentReference ->
                // Log success message with the new document ID
                Log.d("Create note", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            ?.addOnFailureListener { e ->
                // Log error message if the document addition fails
                Log.w("Create note", "Error adding document", e)
            }
    }

    // Log out the current user
    override fun logout() {
        Firebase.auth.signOut()
        // Notify the view that the user has logged out
        iMapView.onLogout()
    }
}
