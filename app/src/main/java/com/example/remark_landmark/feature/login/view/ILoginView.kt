package com.example.remark_landmark.feature.login.view

interface ILoginView {
    fun onClear()
    fun onShowProgress()
    fun onHideProgress()
    fun onLoginSuccess(nickname: String, age: Int)
}