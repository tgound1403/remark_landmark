package com.example.remark_landmark.feature.auth.presenter

interface IAuthPresenter {
    fun showLoading()
    fun hideLoading()
    fun register(email: String, password: String, rePassword: String)
    fun login(email: String, password: String)
}