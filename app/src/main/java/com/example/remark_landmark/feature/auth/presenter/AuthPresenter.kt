package com.example.remark_landmark.feature.auth.presenter

import android.content.ContentValues.TAG
import android.util.Log
import com.example.remark_landmark.feature.auth.model.UserInfoModel
import com.example.remark_landmark.feature.auth.view.IAuthView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

// Presenter class for handling authentication and communicating with the view
class AuthPresenter(var iAuthView: IAuthView) : IAuthPresenter {
    // FirebaseAuth instance
    private lateinit var auth: FirebaseAuth

    // Show loading indicator
    override fun showLoading() {
        iAuthView.onShowLoading()
    }

    // Hide loading indicator
    override fun hideLoading() {
        iAuthView.onHideLoading()
    }

    // Register a new user with email and password
    override fun register(email: String, password: String, rePassword: String) {
        showLoading() // Show loading indicator
        if (password != rePassword) {
            // Check if passwords match
            iAuthView.onAuthError("Password not match, please try again")
            hideLoading() // Hide loading indicator if passwords do not match
            return
        }
        auth = Firebase.auth // Initialize FirebaseAuth instance
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Registration success
                    Log.d(TAG, "createUserWithEmail:success")
                    iAuthView.onAuthSuccess()
                } else {
                    // Registration failure
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    iAuthView.onAuthError(task.exception?.message.toString())
                }
                hideLoading() // Hide loading indicator after operation
            }
    }

    // Login an existing user with email and password
    override fun login(email: String, password: String) {
        showLoading() // Show loading indicator
        auth = Firebase.auth // Initialize FirebaseAuth instance
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login success
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    // Create user info model and update with user's email and a fixed age
                    val userInfoModel = UserInfoModel()
                    userInfoModel.nickname = user!!.email.toString()
                    userInfoModel.age = 22

                    hideLoading() // Hide loading indicator
                    iAuthView.onAuthSuccess()
                } else {
                    // Login failure
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    iAuthView.onAuthError(task.exception?.message.toString())
                }
                hideLoading() // Hide loading indicator after operation
            }
    }
}
