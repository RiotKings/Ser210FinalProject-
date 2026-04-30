package com.example.ser210_final_client.screens

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.ser210_final_client.R
import com.example.ser210_final_client.util.SessionPrefs

class SettingsScreen : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val lightModeSwitch = view.findViewById<Switch>(R.id.lightModeSwitch)
        val logoutButton = view.findViewById<ImageButton>(R.id.logoutButton)
        view.findViewById<TextView>(R.id.settingsLoggedInAsText).text =
            "Logged in as ${SessionPrefs.displayName(requireContext())}"
        val isNightMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        lightModeSwitch.isChecked = !isNightMode
        logoutButton.setImageResource(if (isNightMode) R.drawable.logout_dark else R.drawable.logout_light)

        lightModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }

        logoutButton.setOnClickListener {
            SessionPrefs.clearSession(requireContext())

            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        return view
    }
}