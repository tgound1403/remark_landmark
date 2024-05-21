package com.example.remark_landmark.feature.login.presenter

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import com.example.remark_landmark.core.util.thread.ThreadUtil
import com.example.remark_landmark.feature.login.model.UserInfoModel
import com.example.remark_landmark.feature.login.presenter.controller.LoginController
import com.example.remark_landmark.feature.login.view.ILoginView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginPresenter(var iLoginView: ILoginView) : ILoginPresenter {
    private lateinit var auth: FirebaseAuth
    override fun clear() {
        iLoginView.onClear()
    }

    override fun showProgress() {
        iLoginView.onShowProgress()
    }

    override fun hideProgress() {
        iLoginView.onHideProgress()
    }

    override fun register(email: String, password: String) {
        clear()
        showProgress()
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                }
                hideProgress()
            }
    }


    override fun login(email: String, password: String) {
        clear()
        showProgress()
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    val userInfoModel = UserInfoModel()
                    userInfoModel.nickname = user!!.email.toString()
                    userInfoModel.age = 22

                    hideProgress()
                    iLoginView.onLoginSuccess(
                        nickname = userInfoModel.nickname,
                        age = userInfoModel.age
                    )
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                }
                hideProgress()
            }
    }
}