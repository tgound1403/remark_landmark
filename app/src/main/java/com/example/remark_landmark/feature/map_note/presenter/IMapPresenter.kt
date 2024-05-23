package com.example.remark_landmark.feature.map_note.presenter

import com.example.remark_landmark.feature.map_note.model.MarkerInfoModel

interface IMapPresenter {
    fun searchMarkers(searchValue: String) : List<MarkerInfoModel>
    fun fetchMarkers()
    fun createMarker(owner: String, lat: Double, long: Double, note: String)
    fun logout()
}