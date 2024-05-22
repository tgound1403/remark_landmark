package com.example.remark_landmark.feature.auth.presenter

interface IAuthPresenter {
    fun clear()
    fun showProgress()
    fun hideProgress()
    fun register(email: String, password: String)
    fun login(email: String, password: String)
}