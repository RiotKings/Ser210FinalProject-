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
import androidx.lifecycle.ViewModelProvider
import com.example.ser210_final_client.data.database.AppDatabase
import com.example.ser210_final_client.data.database.Post
import com.example.ser210_final_client.data.database.Response
import com.example.ser210_final_client.model.MainViewModel
import com.example.ser210_final_client.model.MainViewModelFactory
import com.example.ser210_final_client.util.SessionPrefs

class CodeScreen : Fragment() {

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val db = AppDatabase.getInstance(requireContext().applicationContext)

        viewModel = ViewModelProvider(
            requireActivity(),
            MainViewModelFactory(db)
        )[MainViewModel::class.java]

        viewModel.setUser(
            SessionPrefs.displayName(requireContext())
        )

        return ComposeView(requireContext()).apply {
            setContent {
                CodeScreenUI(viewModel)
            }
        }
    }
}

@Composable
fun CodeScreenUI(viewModel: MainViewModel) {

    var posts by remember { mutableStateOf(viewModel.posts) }
    var input by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadPosts("code") {
            posts = viewModel.posts
        }
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
            viewModel.addPost("code", input) {
                posts = viewModel.posts
                input = ""
            }
        }) {
            Text("Post Code")
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(posts) { post ->
                CodePostItem(post, viewModel)
            }
        }
    }
}

@Composable
fun CodePostItem(post: Post, viewModel: MainViewModel) {

    var responses by remember { mutableStateOf(listOf<Response>()) }
    var expanded by remember { mutableStateOf(false) }
    var input by remember { mutableStateOf("") }

    LaunchedEffect(post.id) {
        responses = viewModel.getResponses(post.id)
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
                viewModel.addResponse(post.id, input) {
                    responses = responses + Response(
                        postId = post.id,
                        userId = viewModel.currentUser,
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