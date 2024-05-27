package com.example.remark_landmark.feature.auth.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.remark_landmark.R
import com.example.remark_landmark.feature.auth.presenter.IAuthPresenter
import com.example.remark_landmark.feature.auth.presenter.AuthPresenter
import com.example.remark_landmark.feature.map_note.view.MapActivity

// Activity class for handling authentication views and interactions
class AuthActivity : AppCompatActivity(), IAuthView {
    // UI components
    private lateinit var editTextId: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextRePassword: EditText
    private lateinit var submitBtn: Button
    private lateinit var switchAuthBtn: Button
    private lateinit var textViewLoginResult: TextView
    private lateinit var frameLayoutProgress: FrameLayout

    // Presenter instance
    lateinit var iAuthPresenter: IAuthPresenter

    // Flag to track if the current mode is login or registration
    private var isLogin = true

    // Initialize the activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        initPresenter() // Initialize the presenter
        findView() // Find and bind the UI components
        setListener() // Set listeners for buttons
    }

    // Bind UI components to their respective views
    private fun findView() {
        textViewLoginResult = findViewById(R.id.textViewLoginResult)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextRePassword = findViewById(R.id.editTextRePassword)
        submitBtn = findViewById(R.id.submitBtn)
        switchAuthBtn = findViewById(R.id.switchAuthBtn)
        editTextId = findViewById(R.id.editTextId)
        frameLayoutProgress = findViewById(R.id.frameLayoutProgress)
    }

    // Initialize the presenter
    private fun initPresenter() {
        iAuthPresenter = AuthPresenter(iAuthView = this)
    }

    // Set listeners for buttons to handle authentication actions
    private fun setListener() {
        submitBtn.setOnClickListener {
            if (isLogin) {
                // Handle login
                iAuthPresenter.login(
                    email = editTextId.text.toString().trim(),
                    password = editTextPassword.text.toString().trim()
                )
            } else {
                // Handle registration
                iAuthPresenter.register(
                    email = editTextId.text.toString().trim(),
                    password = editTextPassword.text.toString().trim(),
                    rePassword = editTextRePassword.text.toString().trim()
                )
            }
        }
        switchAuthBtn.setOnClickListener {
            // Toggle between login and registration mode
            isLogin = !isLogin
            changeUI(isLogin)
        }
    }

    // Update UI elements based on the current mode (login or registration)
    private fun changeUI(isLogin: Boolean) {
        if (isLogin) {
            editTextRePassword.visibility = View.GONE
            textViewLoginResult.visibility = View.GONE
            submitBtn.text = "Login"
            switchAuthBtn.text = "Not a member? Register"
        } else {
            editTextId.text.clear()
            editTextPassword.text.clear()
            textViewLoginResult.visibility = View.GONE
            editTextRePassword.visibility = View.VISIBLE
            submitBtn.text = "Register"
            switchAuthBtn.text = "Already a member? Login"
        }
    }

    // Hide loading indicator
    override fun onHideLoading() {
        frameLayoutProgress.visibility = View.GONE
    }

    // Show loading indicator
    override fun onShowLoading() {
        frameLayoutProgress.visibility = View.VISIBLE
    }

    // Handle successful authentication and navigate to the map activity
    override fun onAuthSuccess() {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }

    // Handle authentication error and display error message
    override fun onAuthError(error: String) {
        textViewLoginResult.visibility = View.VISIBLE
        textViewLoginResult.text = error
    }
}
