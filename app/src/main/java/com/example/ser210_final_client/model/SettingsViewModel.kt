package com.example.ser210_final_client.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.ser210_final_client.util.SessionPrefs

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    fun loggedInLine(): String {
        val name = SessionPrefs.displayName(getApplication())
        return "Logged in as $name"
    }

    fun clearSession() {
        SessionPrefs.clearSession(getApplication())
    }
}
