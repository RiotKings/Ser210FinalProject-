package com.example.ser210_final_client.model

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
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

    fun isLightMode(): Boolean {
        val uiMode = getApplication<Application>().resources.configuration.uiMode
        val isNight = (uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES
        return !isNight
    }

    fun setLightMode(enabled: Boolean) {
        if (enabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}