package com.example.remark_landmark.feature.login.presenter

interface ILoginPresenter {
    fun clear()
    fun showProgress()
    fun hideProgress()
    fun register(email: String, password: String)
    fun login(email: String, password: String)
}