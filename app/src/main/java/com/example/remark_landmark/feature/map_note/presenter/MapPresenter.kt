package com.example.remark_landmark.feature.map_note.presenter

import android.content.ContentValues
import android.util.Log
import com.example.remark_landmark.feature.map_note.model.MarkerInfoModel
import com.example.remark_landmark.feature.map_note.view.IMapView
import com.google.firebase.firestore.FirebaseFirestore

class MapPresenter(var iMapView: IMapView) : IMapPresenter {
    private var db: FirebaseFirestore? = null
    private fun useFireStore() {
        db = FirebaseFirestore.getInstance()
        createMarkerNote(db)
    }

    private fun createMarkerNote(db: FirebaseFirestore?) {
        // Create a new user with a first, middle, and last name
        val markerNote = hashMapOf(
            "lat" to 17,
            "long" to 120,
            "note" to "Note from device",
            "owner" to "test",
        )

// Add a new document with a generated ID
        db?.collection("users")
            ?.add(markerNote)
            ?.addOnSuccessListener { documentReference ->
                Log.d("Create marker", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            ?.addOnFailureListener { e ->
                Log.w("Create marker", "Error adding document", e)
            }
    }

    override fun fetchMarkers() {
        useFireStore()
        val binhThuan = MarkerInfoModel(11.0, 107.0, "Hello from dattd linhnt", "dattd linhnt")
        val binhDinh = MarkerInfoModel(13.0, 109.0, "Hello from tienp", "tienp")
        val giaLai = MarkerInfoModel(14.0, 108.0, "Hello from namtt", "namtt")
        val tienGiang = MarkerInfoModel(10.0, 106.0, "Hello from duongnt", "duongnt")

        val defaultLocationList: ArrayList<MarkerInfoModel> = ArrayList()
        defaultLocationList.addAll(setOf(binhThuan, binhDinh, giaLai, tienGiang))

        val documentReference = db!!.collection("locations")

        documentReference.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.documents.isNotEmpty()) {
                Log.d(ContentValues.TAG, "Current data: ${snapshot.documents}")
                val markerList: ArrayList<MarkerInfoModel> = ArrayList()

                markerList.addAll(defaultLocationList)
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

    override fun createMarker() {
        TODO("Not yet implemented")
    }

    override fun getCurrentLocation() {
        TODO("Not yet implemented")
    }
}