package com.example.remark_landmark.feature.auth.presenter.controller

import com.example.remark_landmark.core.util.thread.ThreadUtil

object AuthController {
    interface LoginControllerDelegate {
        fun onSuccess(response: String)
        fun onFailed(error: String)
    }
    fun requestLogin(email: String, password: String, delegate: LoginControllerDelegate) {
        ThreadUtil.startThread {
            Thread.sleep(3000)

            delegate.onSuccess("response from server, user info is a JsonObjectString -> parsing it")
            delegate.onFailed("failed on login")
        }
    }
    fun requestRegister(email: String, password: String, delegate: LoginControllerDelegate) {
        ThreadUtil.startThread {
            Thread.sleep(3000)

            delegate.onSuccess("response from server, user info is a JsonObjectString -> parsing it")
            delegate.onFailed("failed on login")
        }
    }
}