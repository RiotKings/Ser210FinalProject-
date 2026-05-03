package com.example.ser210_final_client.model

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ser210_final_client.data.api.ApiInterface
import com.example.ser210_final_client.data.database.AppDatabase
import com.example.ser210_final_client.data.database.RegisteredUser
import com.example.ser210_final_client.util.PasswordHasher
import com.example.ser210_final_client.util.SessionPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(
    application: Application,
    private val db: AppDatabase
) : AndroidViewModel(application) {

    fun login(username: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val label = withContext(Dispatchers.IO) {
                resolveLoginLabel(username, password)
            }
            if (label == null) {
                onResult(false)
                return@launch
            }
            val allUsers = withContext(Dispatchers.IO) {
                try {
                    ApiInterface.create().getUsers().body()?.results.orEmpty()
                } catch (_: Exception) {
                    emptyList()
                }
            }
            val userIndex = allUsers.indexOfFirst {
                it.login.username.equals(username, ignoreCase = true)
            }.coerceAtLeast(0)
            getApplication<Application>().getSharedPreferences(SessionPrefs.PREFS_NAME, Application.MODE_PRIVATE)
                .edit()
                .putBoolean(SessionPrefs.KEY_LOGGED_IN, true)
                .putString(SessionPrefs.KEY_DISPLAY_NAME, label)
                .putInt(SessionPrefs.KEY_USER_INDEX, userIndex)
                .remove(SessionPrefs.KEY_PROFILE_IMAGE)
                .apply()
            onResult(true)
        }
    }

    fun signUp(username: String, password: String, onResult: (SignUpResult) -> Unit) {
        viewModelScope.launch {
            val taken = withContext(Dispatchers.IO) {
                db.userAccountDao().findByUsernameIgnoreCase(username) != null
            }
            if (taken) {
                onResult(SignUpResult.Taken)
                return@launch
            }
            val ok = withContext(Dispatchers.IO) {
                try {
                    db.userAccountDao().insert(
                        RegisteredUser(username, PasswordHasher.hash(username, password))
                    )
                    true
                } catch (_: SQLiteConstraintException) {
                    false
                }
            }
            if (!ok) {
                onResult(SignUpResult.Taken)
                return@launch
            }
            getApplication<Application>().getSharedPreferences(SessionPrefs.PREFS_NAME, Application.MODE_PRIVATE)
                .edit()
                .putBoolean(SessionPrefs.KEY_LOGGED_IN, true)
                .putString(SessionPrefs.KEY_DISPLAY_NAME, username)
                .apply()
            onResult(SignUpResult.Ok)
        }
    }

    private suspend fun resolveLoginLabel(username: String, password: String): String? {
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

    enum class SignUpResult { Ok, Taken }
}
