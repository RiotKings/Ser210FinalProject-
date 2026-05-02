package com.example.ser210_final_client

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.example.ser210_final_client.data.UserRepository
import com.example.ser210_final_client.screens.ChatScreen
import com.example.ser210_final_client.screens.CodeScreen
import com.example.ser210_final_client.screens.HomeScreen
import com.example.ser210_final_client.screens.LoginActivity
import com.example.ser210_final_client.screens.MemesScreen
import com.example.ser210_final_client.screens.Q_AScreen
import com.example.ser210_final_client.screens.RepoScreen
import com.example.ser210_final_client.screens.SettingsScreen
import com.example.ser210_final_client.screens.SplashActivity
import com.example.ser210_final_client.util.SessionPrefs
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var isExpanded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!intent.getBooleanExtra("from_auth_flow", false)) {
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        // Load HomeScreen by default
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, HomeScreen())
                .commit()
        }

        val sidebar = findViewById<LinearLayout>(R.id.sidebar)
        val btnExpand = findViewById<ImageButton>(R.id.btnExpand)

        val labels = listOf(
            R.id.label_home,
            R.id.label_chat,
            R.id.label_code,
            R.id.label_memes,
            R.id.label_qa,
            R.id.label_repo,
            R.id.label_settings
        )

        // Load profile image into sidebar
        loadProfileImage()

        // Set initials as fallback while image loads
        val tvAvatar = findViewById<TextView>(R.id.tv_profile_avatar)
        tvAvatar.text = SessionPrefs.initials(this)

        // Toggle expand/collapse
        btnExpand.setOnClickListener {
            isExpanded = !isExpanded

            labels.forEach { id ->
                findViewById<TextView>(id).visibility =
                    if (isExpanded) View.VISIBLE else View.GONE
            }

            val params = sidebar.layoutParams
            params.width = if (isExpanded)
                (200 * resources.displayMetrics.density).toInt()
            else
                (56 * resources.displayMetrics.density).toInt()
            sidebar.layoutParams = params

            btnExpand.setImageResource(
                if (isExpanded) R.drawable.ic_arrow_left else R.drawable.ic_arrow_right
            )
        }

        // Navigation
        fun navigate(screen: androidx.fragment.app.Fragment) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, screen)
                .commit()
        }

        findViewById<LinearLayout>(R.id.mini_nav_home).setOnClickListener     { navigate(HomeScreen()) }
        findViewById<LinearLayout>(R.id.mini_nav_chat).setOnClickListener     { navigate(ChatScreen()) }
        findViewById<LinearLayout>(R.id.mini_nav_code).setOnClickListener     { navigate(CodeScreen()) }
        findViewById<LinearLayout>(R.id.mini_nav_memes).setOnClickListener    { navigate(MemesScreen()) }
        findViewById<LinearLayout>(R.id.mini_nav_qa).setOnClickListener       { navigate(Q_AScreen()) }
        findViewById<LinearLayout>(R.id.mini_nav_repo).setOnClickListener     { navigate(RepoScreen()) }
        findViewById<LinearLayout>(R.id.mini_nav_settings).setOnClickListener { navigate(SettingsScreen()) }
    }

    private fun loadProfileImage() {
        val ivProfilePic = findViewById<ImageView>(R.id.iv_profile_pic)
        val tvAvatar = findViewById<TextView>(R.id.tv_profile_avatar)

        // Check if we already have a cached image URL
        val cachedUrl = SessionPrefs.getProfileImage(this)
        if (cachedUrl != null) {
            tvAvatar.visibility = View.GONE
            ivProfilePic.visibility = View.VISIBLE
            ivProfilePic.load(cachedUrl) {
                transformations(CircleCropTransformation())
            }
            return
        }

        // Otherwise fetch from API
        lifecycleScope.launch {
            val userIndex = SessionPrefs.getUserIndex(this@MainActivity)
            val user = UserRepository.getUserById(userIndex)
            user?.let {
                val imageUrl = it.picture.large
                SessionPrefs.saveProfileImage(this@MainActivity, imageUrl)
                tvAvatar.visibility = View.GONE
                ivProfilePic.visibility = View.VISIBLE
                ivProfilePic.load(imageUrl) {
                    transformations(CircleCropTransformation())
                }
            }
        }
    }
}