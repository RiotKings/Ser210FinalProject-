package com.example.ser210_final_client.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ser210_final_client.MainActivity
import com.example.ser210_final_client.R

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val confirmButton = findViewById<Button>(R.id.signupConfirmButton)
        val loginText = findViewById<TextView>(R.id.signupLoginText)

        confirmButton.setOnClickListener {
            getSharedPreferences("code_gram_session", MODE_PRIVATE)
                .edit()
                .putBoolean("logged_in", true)
                .apply()

            startActivity(Intent(this, MainActivity::class.java).putExtra("from_auth_flow", true))
            finishAffinity()
        }

        loginText.setOnClickListener {
            finish()
        }
    }
}
