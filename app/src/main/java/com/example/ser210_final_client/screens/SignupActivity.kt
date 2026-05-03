package com.example.ser210_final_client.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.ser210_final_client.MainActivity
import com.example.ser210_final_client.R
import com.example.ser210_final_client.data.database.AppDatabase
import com.example.ser210_final_client.model.AuthViewModel
import com.example.ser210_final_client.model.AuthViewModelFactory

class SignupActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val db = AppDatabase.getInstance(applicationContext)
        authViewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(application, db)
        )[AuthViewModel::class.java]

        val confirmButton = findViewById<Button>(R.id.signupConfirmButton)
        val loginText = findViewById<TextView>(R.id.signupLoginText)
        val usernameField = findViewById<EditText>(R.id.signupUsernameInput)
        val passwordField = findViewById<EditText>(R.id.signupPasswordInput)
        val confirmPasswordField = findViewById<EditText>(R.id.signupPasswordConfirmInput)

        confirmButton.setOnClickListener {
            val username = usernameField.text.toString().trim()
            val password = passwordField.text.toString()
            val confirmPassword = confirmPasswordField.text.toString()
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authViewModel.signUp(username, password) { result ->
                when (result) {
                    AuthViewModel.SignUpResult.Taken ->
                        Toast.makeText(this, "That username is already taken", Toast.LENGTH_SHORT).show()
                    AuthViewModel.SignUpResult.Ok -> {
                        startActivity(Intent(this, MainActivity::class.java).putExtra("from_auth_flow", true))
                        finishAffinity()
                    }
                }
            }
        }

        loginText.setOnClickListener {
            finish()
        }
    }
}
