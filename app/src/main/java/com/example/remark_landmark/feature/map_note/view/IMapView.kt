package com.example.remark_landmark.feature.map_note.view

import android.location.Location
import com.example.remark_landmark.feature.map_note.model.MarkerInfoModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

interface IMapView {
    fun onFetchMarkerSuccess(markers: List<MarkerInfoModel>)
    fun onCreateMarkerSuccess()
    fun onFoundMarker()
    fun onSelectMarker()
    fun onShowDialog()
    fun onGetLocationSuccess(location: Location?)
    fun onLogout()
}