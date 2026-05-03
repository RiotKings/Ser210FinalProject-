package com.example.ser210_final_client.screens

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import coil.load
import coil.transform.CircleCropTransformation
import com.example.ser210_final_client.data.UserRepository
import com.example.ser210_final_client.util.SessionPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SettingsScreen : Fragment() {

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setContent {
                ProfileUI()
            }
        }
    }
}

@Composable
fun ProfileUI() {

    val context = LocalContext.current

    val username = SessionPrefs.displayName(context)
    val profileImage = SessionPrefs.getProfileImage(context)

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val userIndex = SessionPrefs.getUserIndex(context)

        val user = withContext(Dispatchers.IO) {
            UserRepository.getUserById(userIndex)
        }

        user?.let {
            fullName = "${it.name.first} ${it.name.last}"
            email = it.email
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {

        Text("Profile", color = Color.White, fontSize = 22.sp)

        Spacer(modifier = Modifier.height(16.dp))

        AndroidView(
            factory = {
                if (profileImage != null) {
                    ImageView(it).apply {
                        layoutParams = ViewGroup.LayoutParams(200, 200)
                        load(profileImage) {
                            transformations(CircleCropTransformation())
                        }
                    }
                } else {
                    TextView(it).apply {
                        text = SessionPrefs.initials(it)
                        textSize = 24f
                        setTextColor(android.graphics.Color.WHITE)
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Username: $username", color = Color.White, fontSize = 18.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Text("Full Name: $fullName", color = Color.White, fontSize = 16.sp)

        Spacer(modifier = Modifier.height(4.dp))

        Text("Email: $email", color = Color.Gray, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Initials: ${SessionPrefs.initials(context)}",
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}