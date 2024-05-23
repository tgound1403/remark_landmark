package com.example.remark_landmark.feature.auth.presenter

import android.content.ContentValues.TAG
import android.util.Log
import com.example.remark_landmark.feature.auth.model.UserInfoModel
import com.example.remark_landmark.feature.auth.view.IAuthView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthPresenter(var iAuthView: IAuthView) : IAuthPresenter {
    private lateinit var auth: FirebaseAuth

    override fun showLoading() {
        iAuthView.onShowLoading()
    }

    override fun hideLoading() {
        iAuthView.onHideLoading()
    }

    override fun register(email: String, password: String, rePassword: String) {
        showLoading()
        if (password != rePassword) {
            iAuthView.onAuthError("Password not match, please try again")
            hideLoading()
            return
        }
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    iAuthView.onAuthSuccess()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    iAuthView.onAuthError(task.exception?.message.toString())
                }
                hideLoading()
            }
    }


    override fun login(email: String, password: String) {
        showLoading()
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

                    hideLoading()
                    iAuthView.onAuthSuccess()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    iAuthView.onAuthError(task.exception?.message.toString())
                }
                hideLoading()
            }
    }
}