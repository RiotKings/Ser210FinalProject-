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

class LoginActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val db = AppDatabase.getInstance(applicationContext)
        authViewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(application, db)
        )[AuthViewModel::class.java]

        val confirmButton = findViewById<Button>(R.id.loginConfirmButton)
        val signUpText = findViewById<TextView>(R.id.loginSignUpText)
        val usernameField = findViewById<EditText>(R.id.loginUsernameButton)
        val passwordField = findViewById<EditText>(R.id.loginPasswordButton)

        confirmButton.setOnClickListener {
            val username = usernameField.text.toString().trim()
            val password = passwordField.text.toString()
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authViewModel.login(username, password) { ok ->
                if (!ok) {
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                } else {
                    startActivity(Intent(this, MainActivity::class.java).putExtra("from_auth_flow", true))
                    finish()
                }
            }
        }

        signUpText.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}
