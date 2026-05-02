package com.example.ser210_final_client.util

import android.content.Context

object SessionPrefs {
    const val PREFS_NAME = "code_gram_session"
    const val KEY_LOGGED_IN = "logged_in"
    const val KEY_DISPLAY_NAME = "session_display_name"

    fun displayName(context: Context): String {
        val raw = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_DISPLAY_NAME, null)?.trim().orEmpty()
        return raw.ifEmpty { "user_001" }
    }

    fun clearSession(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_LOGGED_IN, false)
            .remove(KEY_DISPLAY_NAME)
            .apply()
    }
}
