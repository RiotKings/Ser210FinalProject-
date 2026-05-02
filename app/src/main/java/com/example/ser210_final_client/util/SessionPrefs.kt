package com.example.ser210_final_client.util

import android.content.Context

object SessionPrefs {
    const val PREFS_NAME = "code_gram_session"
    const val KEY_LOGGED_IN = "logged_in"
    const val KEY_DISPLAY_NAME = "session_display_name"
    const val KEY_USER_INDEX = "user_index"
    const val KEY_PROFILE_IMAGE = "profile_image_url"

    fun displayName(context: Context): String {
        val raw = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_DISPLAY_NAME, null)?.trim().orEmpty()
        return raw.ifEmpty { "user_001" }
    }

    fun initials(context: Context): String {
        val name = displayName(context)
        val parts = name.trim().split(" ")
        return if (parts.size >= 2) {
            "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
        } else {
            name.take(2).uppercase()
        }
    }

    fun saveUserIndex(context: Context, index: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putInt(KEY_USER_INDEX, index)
            .apply()
    }

    fun getUserIndex(context: Context): Int {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_USER_INDEX, 0)
    }

    fun saveProfileImage(context: Context, url: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_PROFILE_IMAGE, url)
            .apply()
    }

    fun getProfileImage(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_PROFILE_IMAGE, null)
    }

    fun clearSession(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_LOGGED_IN, false)
            .remove(KEY_DISPLAY_NAME)
            .remove(KEY_USER_INDEX)
            .remove(KEY_PROFILE_IMAGE)
            .apply()
    }
}