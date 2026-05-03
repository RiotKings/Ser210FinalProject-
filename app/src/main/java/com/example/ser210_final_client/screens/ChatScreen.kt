package com.example.ser210_final_client.screens

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.example.ser210_final_client.data.api.ApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatScreen : Fragment() {

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ChatScreenUI()
            }
        }
    }
}

@Composable
fun ChatScreenUI() {

    var usernames by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(Unit) {
        val users = withContext(Dispatchers.IO) {
            try {
                ApiInterface.create().getUsers().body()?.results ?: emptyList()
            } catch (_: Exception) {
                emptyList()
            }
        }
        usernames = users.map { it.login.username }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Text(
            text = "Chat w/ Grammers",
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(usernames) { name ->
                ChatUserItem(name)
            }
        }
    }
}

@Composable
fun ChatUserItem(username: String) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .background(Color(0xFF6D6D6D), shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color.LightGray, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = username.firstOrNull()?.uppercase() ?: "T",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = username,
            color = Color.White,
            fontSize = 16.sp
        )
    }
}