package com.example.remark_landmark.feature.auth.view

interface IAuthView {
    fun onClear()
    fun onShowProgress()
    fun onHideProgress()
    fun onChooseLogin()
    fun onChooseRegister()
    fun onLoginSuccess(nickname: String, age: Int)
}