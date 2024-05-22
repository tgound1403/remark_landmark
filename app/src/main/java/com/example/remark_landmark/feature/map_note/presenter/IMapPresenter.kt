package com.example.remark_landmark.feature.map_note.presenter

interface IMapPresenter {
    fun fetchMarkers()
    fun createMarker(owner: String, lat: Double, long: Double, note: String)
    fun getCurrentLocation()
}