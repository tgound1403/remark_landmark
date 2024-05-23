package com.example.remark_landmark.feature.auth.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.ui.graphics.Color
import com.example.remark_landmark.feature.auth.presenter.AuthPresenter
import com.example.remark_landmark.feature.auth.presenter.IAuthPresenter
import com.example.remark_landmark.feature.map_note.view.MapActivity

class ComposeAuthActivity : ComponentActivity(), IAuthView {
    private lateinit var iAuthPresenter: IAuthPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPresenter()
        setContent {
            AuthScreen(iAuthPresenter, "")
        }
    }

    private fun initPresenter() {
        iAuthPresenter = AuthPresenter(iAuthView = this)
    }

    override fun onShowLoading() {
        setContent {
            LoadingScreen()
        }
    }

    override fun onHideLoading() {
        setContent {
            AuthScreen(iAuthPresenter, "")
        }
    }

    override fun onAuthSuccess() {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }

    override fun onAuthError(error: String) {
        Log.e("AuthError", error)
        setContent {
            AuthScreen(iAuthPresenter, error)
        }
    }
}

@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun AuthScreen(iAuthPresenter: IAuthPresenter, error: String) {
    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to Remark Landmark",
            fontSize = 24.sp,
            style = MaterialTheme.typography.h5
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text("abc@gmail.com", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            placeholder = { Text("Your password please", color = Color.Gray) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (!isLogin) {
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(text = error)

        Spacer(modifier = Modifier.height(4.dp))

        TextButton(onClick = { isLogin = !isLogin }) {
            Text(text = if (isLogin) "Don't have an account? Register" else "Already have an account? Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        val isEnabled = email.isNotEmpty() && password.isNotEmpty()
        Button(
            enabled = isEnabled,
            onClick = {
                if (isEnabled) {
                    if (isLogin) {
                        iAuthPresenter.login(
                            email = email,
                            password = password
                        )
                    } else {
                        iAuthPresenter.register(
                            email = email,
                            password = password,
                            rePassword = confirmPassword
                        )
                    }
                } else return@Button
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (isEnabled) Color.Blue else Color.Gray,
                contentColor = Color.White // Optional: Set content color
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
        ) {
            Text(text = if (isLogin) "Login" else "Register")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
//    AuthScreen()
}
