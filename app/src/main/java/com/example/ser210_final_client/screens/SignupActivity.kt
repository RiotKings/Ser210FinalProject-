package com.example.ser210_final_client.screens

import android.content.Intent
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ser210_final_client.MainActivity
import com.example.ser210_final_client.R
import com.example.ser210_final_client.data.database.AppDatabase
import com.example.ser210_final_client.data.database.RegisteredUser
import com.example.ser210_final_client.util.PasswordHasher
import com.example.ser210_final_client.util.SessionPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

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

            lifecycleScope.launch {
                val taken = withContext(Dispatchers.IO) {
                    AppDatabase.getInstance(applicationContext)
                        .userAccountDao()
                        .findByUsernameIgnoreCase(username) != null
                }
                if (taken) {
                    Toast.makeText(this@SignupActivity, "That username is already taken", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val inserted = withContext(Dispatchers.IO) {
                    try {
                        AppDatabase.getInstance(applicationContext).userAccountDao().insert(
                            RegisteredUser(
                                username = username,
                                passwordHash = PasswordHasher.hash(username, password)
                            )
                        )
                        true
                    } catch (_: SQLiteConstraintException) {
                        false
                    }
                }
                if (!inserted) {
                    Toast.makeText(this@SignupActivity, "That username is already taken", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                getSharedPreferences(SessionPrefs.PREFS_NAME, MODE_PRIVATE).edit()
                    .putBoolean(SessionPrefs.KEY_LOGGED_IN, true)
                    .putString(SessionPrefs.KEY_DISPLAY_NAME, username)
                    .apply()

                startActivity(Intent(this@SignupActivity, MainActivity::class.java).putExtra("from_auth_flow", true))
                finishAffinity()
            }
        }

        loginText.setOnClickListener {
            finish()
        }
    }
}
