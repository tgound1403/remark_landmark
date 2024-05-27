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

// Activity class for handling authentication views and interactions
class ComposeAuthActivity : ComponentActivity(), IAuthView {
    // Presenter instance
    private lateinit var iAuthPresenter: IAuthPresenter

    // Lifecycle method called when the activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the presenter
        initPresenter()
        // Set the content of the activity using Jetpack Compose
        setContent {
            // Display authentication screen
            AuthScreen(iAuthPresenter, "")
        }
    }

    // Initialize the presenter
    private fun initPresenter() {
        iAuthPresenter = AuthPresenter(iAuthView = this)
    }

    // Called when loading indicator should be shown
    override fun onShowLoading() {
        // Set the content to display the loading screen
        setContent {
            LoadingScreen()
        }
    }

    // Called when loading indicator should be hidden
    override fun onHideLoading() {
        // Set the content to display the authentication screen again
        setContent {
            AuthScreen(iAuthPresenter, "")
        }
    }

    // Called when authentication is successful
    override fun onAuthSuccess() {
        // Redirect to the map activity
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }

    // Called when an authentication error occurs
    override fun onAuthError(error: String) {
        // Log the error
        Log.e("AuthError", error)
        // Display the authentication screen with error message
        setContent {
            AuthScreen(iAuthPresenter, error)
        }
    }
}

// Composable function for the loading screen
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

// Composable function for the authentication screen
@Composable
fun AuthScreen(iAuthPresenter: IAuthPresenter, error: String) {
    // State variables for email, password, and registration confirmation
    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Column layout for authentication screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Welcome to Remark Landmark",
            fontSize = 24.sp,
            style = MaterialTheme.typography.h5
        )

        // Spacing
        Spacer(modifier = Modifier.height(16.dp))

        // Email input field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text("abc@gmail.com", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth()
        )

        // Spacing
        Spacer(modifier = Modifier.height(8.dp))

        // Password input field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            placeholder = { Text("Your password please", color = Color.Gray) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        // Conditional rendering for registration confirmation input field
        if (!isLogin) {
            // Spacing
            Spacer(modifier = Modifier.height(8.dp))

            // Confirmation password input field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Error message
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = error)

        // Spacing
        Spacer(modifier = Modifier.height(4.dp))

        // Toggle between login and registration
        TextButton(onClick = { isLogin = !isLogin }) {
            Text(text = if (isLogin) "Don't have an account? Register" else "Already have an account? Login")
        }

        // Spacing
        Spacer(modifier = Modifier.height(16.dp))

        // Button for login or registration
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

// Preview function for the loading screen
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LoadingScreen()
}
