package com.example.ser210_final_client.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ser210_final_client.MainActivity
import com.example.ser210_final_client.R
import com.example.ser210_final_client.data.api.ApiInterface
import com.example.ser210_final_client.data.database.AppDatabase
import com.example.ser210_final_client.util.PasswordHasher
import com.example.ser210_final_client.util.SessionPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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

            lifecycleScope.launch {
                val authorLabel = withContext(Dispatchers.IO) { resolveLoginLabel(username, password) }
                if (authorLabel == null) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Invalid username or password",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                getSharedPreferences(SessionPrefs.PREFS_NAME, MODE_PRIVATE).edit()
                    .putBoolean(SessionPrefs.KEY_LOGGED_IN, true)
                    .putString(SessionPrefs.KEY_DISPLAY_NAME, authorLabel)
                    .apply()

                startActivity(Intent(this@LoginActivity, MainActivity::class.java).putExtra("from_auth_flow", true))
                finish()
            }
        }

        signUpText.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private suspend fun resolveLoginLabel(username: String, password: String): String? {
        val db = AppDatabase.getInstance(applicationContext)
        val local = db.userAccountDao().findByUsernameIgnoreCase(username.trim())
        if (local != null) {
            return if (PasswordHasher.verify(local.username, password, local.passwordHash)) {
                local.username
            } else {
                null
            }
        }
        return fetchApiLoginLabel(username, password)
    }

    private suspend fun fetchApiLoginLabel(username: String, password: String): String? {
        return try {
            val response = ApiInterface.create().getUsers()
            val match = response.body()?.results?.orEmpty()?.find {
                it.login.username.equals(username, ignoreCase = true) &&
                    it.login.password == password
            }
            match?.let {
                val u = it.login.username.trim()
                val f = it.name.first.trim()
                u.ifEmpty { f }.ifEmpty { null }
            }
        } catch (_: Exception) {
            null
        }
    }
}
