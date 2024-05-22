package com.example.remark_landmark.feature.map_note.view

import com.example.remark_landmark.feature.map_note.model.MarkerInfoModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

interface IMapView {
    fun onFetchMarkerSuccess(markers: List<MarkerInfoModel>)
    fun onCreateMarkerSuccess()
    fun onFoundMarker()
    fun onSelectMarker()
    fun onGenerateMarker(note: String, map: GoogleMap, location: LatLng)
}