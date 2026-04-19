package com.example.ser210_final_client

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.ser210_final_client.screens.ChatScreen
import com.example.ser210_final_client.screens.CodeScreen
import com.example.ser210_final_client.screens.HomeScreen
import com.example.ser210_final_client.screens.MemesScreen
import com.example.ser210_final_client.screens.Q_AScreen
import com.example.ser210_final_client.screens.RepoScreen
import com.example.ser210_final_client.screens.SettingsScreen
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)

        // Load HomeScreen by default
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, HomeScreen())
                .commit()
        }

        // Handle sidebar item clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->
            val fragment = when (menuItem.itemId) {
                R.id.nav_home     -> HomeScreen()
                R.id.nav_chat     -> ChatScreen()
                R.id.nav_code     -> CodeScreen()
                R.id.nav_memes    -> MemesScreen()
                R.id.nav_qa       -> Q_AScreen()
                R.id.nav_repo     -> RepoScreen()
                R.id.nav_settings -> SettingsScreen()
                else -> HomeScreen()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit()
            drawerLayout.closeDrawers()
            true
        }
    }
}