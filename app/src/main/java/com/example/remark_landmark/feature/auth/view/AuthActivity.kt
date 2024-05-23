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

class AuthActivity : AppCompatActivity(), IAuthView {
    private lateinit var editTextId: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextRePassword: EditText
    private lateinit var submitBtn: Button
    private lateinit var switchAuthBtn: Button
    private lateinit var textViewLoginResult: TextView
    private lateinit var frameLayoutProgress: FrameLayout

    lateinit var iLoginPresenter: IAuthPresenter

    private var isLogin = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        initPresenter()
        findView()
        setListener()
    }

    private fun findView() {
        textViewLoginResult = findViewById(R.id.textViewLoginResult)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextRePassword = findViewById(R.id.editTextRePassword)
        submitBtn = findViewById(R.id.submitBtn)
        switchAuthBtn = findViewById(R.id.switchAuthBtn)
        editTextId = findViewById(R.id.editTextId)
        frameLayoutProgress = findViewById(R.id.frameLayoutProgress)
    }

    private fun initPresenter() {
        iLoginPresenter = AuthPresenter(iAuthView = this)
    }

    private fun setListener() {
        submitBtn.setOnClickListener {
            if (isLogin) {
                iLoginPresenter.login(
                    email = editTextId.text.toString().trim(),
                    password = editTextPassword.text.toString().trim()
                )
            } else {
                iLoginPresenter.register(
                    email = editTextId.text.toString().trim(),
                    password = editTextPassword.text.toString().trim(),
                    rePassword = editTextRePassword.text.toString().trim()
                )
            }
        }
        switchAuthBtn.setOnClickListener {
            isLogin = !isLogin
            changeUI(isLogin)
        }
    }

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

    override fun onHideLoading() {
        frameLayoutProgress.visibility = View.GONE
    }

    override fun onShowLoading() {
        frameLayoutProgress.visibility = View.VISIBLE
    }

    override fun onAuthSuccess() {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }

    override fun onAuthError(error: String) {
        textViewLoginResult.visibility = View.VISIBLE
        textViewLoginResult.text = error
    }
}