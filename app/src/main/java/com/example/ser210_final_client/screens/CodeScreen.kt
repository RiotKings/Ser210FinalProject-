package com.example.ser210_final_client.screens

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.ser210_final_client.data.api.ApiInterface
import com.example.ser210_final_client.data.database.AppDatabase
import com.example.ser210_final_client.data.database.Post
import com.example.ser210_final_client.data.database.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class CodeScreen : Fragment() {

    private val db by lazy {
        Room.databaseBuilder(
            requireContext().applicationContext,
            AppDatabase::class.java,
            "code_gram_db"
        ).build()
    }

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                CodeScreenUI(db, viewLifecycleOwner)
            }
        }
    }
}

@Composable
fun CodeScreenUI(db: AppDatabase, lifecycleOwner: LifecycleOwner) {

    var posts by remember { mutableStateOf(listOf<Post>()) }
    var input by remember { mutableStateOf("") }
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

    LaunchedEffect(Unit) {
        val data = withContext(Dispatchers.IO) {
            db.postDao().getAllPosts()
                .filter { it.type == "code" }
                .sortedByDescending { it.id }
        }
        posts = data
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {

        Text("Code Feed", color = Color.White, fontSize = 20.sp)

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = input,
            onValueChange = { input = it },
            placeholder = { Text("Enter code...") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            if (input.isBlank()) return@Button

            val username = if (usernames.isNotEmpty()) {
                usernames[Random.nextInt(usernames.size)]
            } else {
                "user_001"
            }

            lifecycleOwner.lifecycleScope.launch {
                val id = withContext(Dispatchers.IO) {
                    db.postDao().insertPost(
                        Post(
                            userId = username,
                            content = input,
                            type = "code",
                            imageUrl = null
                        )
                    ).toInt()
                }

                posts = listOf(
                    Post(id, username, input, "code", null)
                ) + posts

                input = ""
            }

        }) {
            Text("Post Code")
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn {
            items(posts) { post ->
                CodePostItem(post, db, lifecycleOwner)
            }
        }
    }
}

@Composable
fun CodePostItem(post: Post, db: AppDatabase, lifecycleOwner: LifecycleOwner) {

    var responses by remember { mutableStateOf(listOf<Response>()) }
    var expanded by remember { mutableStateOf(false) }
    var input by remember { mutableStateOf("") }

    LaunchedEffect(post.id) {
        responses = withContext(Dispatchers.IO) {
            db.postDao().getResponsesForPost(post.id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .background(Color(0xFF6D6D6D), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {

        Text(post.userId, color = Color.White, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(6.dp))

        Text(post.content, color = Color.White, fontSize = 16.sp)

        Spacer(modifier = Modifier.height(6.dp))

        Button(onClick = { expanded = !expanded }) {
            Text(if (expanded) "Hide Responses" else "See Responses")
        }

        if (expanded) {

            responses.forEach {
                Text(it.content, color = Color.LightGray, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))

            TextField(
                value = input,
                onValueChange = { input = it },
                placeholder = { Text("Reply...") }
            )

            Button(onClick = {
                if (input.isBlank()) return@Button

                lifecycleOwner.lifecycleScope.launch {

                    withContext(Dispatchers.IO) {
                        db.postDao().insertResponse(
                            Response(
                                postId = post.id,
                                userId = "user_001",
                                content = input
                            )
                        )
                    }

                    responses = responses + Response(
                        postId = post.id,
                        userId = "user_001",
                        content = input
                    )

                    input = ""
                }

            }) {
                Text("Send")
            }
        }
    }
}