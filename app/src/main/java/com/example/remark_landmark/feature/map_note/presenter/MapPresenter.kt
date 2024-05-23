package com.example.remark_landmark.feature.map_note.presenter

import android.content.ContentValues
import android.util.Log
import com.example.remark_landmark.feature.map_note.model.MarkerInfoModel
import com.example.remark_landmark.feature.map_note.view.IMapView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MapPresenter(var iMapView: IMapView) : IMapPresenter {
    private var db: FirebaseFirestore? = null
    private fun useFireStore() {
        db = FirebaseFirestore.getInstance()
    }

    override fun searchMarkers(searchValue: String) : List<MarkerInfoModel> {
        useFireStore()
        val documentReference = db!!.collection("notes")
        documentReference.whereEqualTo("note", searchValue)
        return ArrayList()
    }

    override fun fetchMarkers() {
        useFireStore()
        val documentReference = db!!.collection("notes")

        documentReference.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.documents.isNotEmpty()) {
                Log.d(ContentValues.TAG, "Current data: ${snapshot.documents}")
                val markerList: ArrayList<MarkerInfoModel> = ArrayList()

                try {
                    for (document in snapshot.documents) {
                        Log.d(
                            "Marker data",
                            "marker data: ${document.id} ${document.data} $document"
                        )
                        val lat = document.data?.get("lat").toString().toDouble()
                        val long = document.data?.get("long").toString().toDouble()
                        val note = document.data?.get("note").toString()
                        val owner = document.data?.get("owner").toString()
                        val marker = MarkerInfoModel(lat = lat, long = long, note = note, owner = owner)
                        markerList.add(marker)
                    }
                } catch (e: Exception) {
                    Log.d("Error in fetch", e.toString())
                }

                iMapView.onFetchMarkerSuccess(markerList)
            } else {
                Log.d(ContentValues.TAG, "Current data: null")
            }
        }
    }

    override fun createMarker(owner: String, lat: Double, long: Double, note: String) {
        useFireStore()
        // Create a new user with a first, middle, and last name
        val markerNote = hashMapOf(
            "lat" to lat,
            "long" to long,
            "note" to note,
            "owner" to owner,
        )

        // Add a new document with a generated ID
        db?.collection("notes")
            ?.add(markerNote)
            ?.addOnSuccessListener { documentReference ->
                Log.d("Create note", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            ?.addOnFailureListener { e ->
                Log.w("Create note", "Error adding document", e)
            }
    }

    override fun logout() {
        Firebase.auth.signOut()
        iMapView.onLogout()
    }
}