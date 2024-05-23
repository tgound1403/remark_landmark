package com.example.remark_landmark.feature.auth.view

interface IAuthView {
    fun onShowLoading()
    fun onHideLoading()
    fun onAuthSuccess()
    fun onAuthError(error: String)
}