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

class AuthActivity : AppCompatActivity(), IAuthView{
    private lateinit var editTextId : EditText
    private lateinit var editTextPassword : EditText
    private lateinit var loginBtn : Button
    private lateinit var textViewLoginResult : TextView
    private lateinit var frameLayoutProgress : FrameLayout

    lateinit var  iLoginPresenter: IAuthPresenter

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
        loginBtn = findViewById(R.id.loginBtn)
        editTextId = findViewById(R.id.editTextId)
        frameLayoutProgress = findViewById(R.id.frameLayoutProgress)
    }

    private fun initPresenter() {
        iLoginPresenter = AuthPresenter(iAuthView = this)
    }

    private fun setListener() {
        loginBtn.setOnClickListener {
            iLoginPresenter.login(email = editTextId.text.toString().trim(), password = editTextPassword.text.toString().trim())
        }
    }

    override fun onClear() {
        editTextId.setText("")
        editTextPassword.setText("")
    }

    override fun onHideProgress() {
        frameLayoutProgress.visibility = View.GONE
    }

    override fun onChooseLogin() {
        TODO("Not yet implemented")
    }

    override fun onChooseRegister() {
        TODO("Not yet implemented")
    }

    override fun onShowProgress() {
        frameLayoutProgress.visibility = View.VISIBLE
    }

    override fun onLoginSuccess(nickname: String, age: Int) {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }
}