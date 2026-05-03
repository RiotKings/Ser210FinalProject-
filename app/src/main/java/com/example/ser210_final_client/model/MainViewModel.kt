package com.example.ser210_final_client.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ser210_final_client.data.database.AppDatabase
import com.example.ser210_final_client.data.database.Post
import com.example.ser210_final_client.data.database.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.ser210_final_client.model.Repo

class MainViewModel(private val db: AppDatabase) : ViewModel() {

    // Holds the current logged-in user for the whole app
    var currentUser: String = "user_001"

    // Allows the user to be changed, for example after login
    fun setUser(username: String) {
        currentUser = username
    }

    // Stores the list of posts currently shown on screen
    var posts: List<Post> = emptyList()
        private set

    // Loads posts from the database based on type such as code, meme, or question
    fun loadPosts(type: String, onDone: () -> Unit) {
        viewModelScope.launch {
            posts = withContext(Dispatchers.IO) {
                db.postDao().getAllPosts()
                    .filter { it.type == type }
                    .sortedByDescending { it.id }
            }
            onDone()
        }
    }

    // Adds a new post using the current user instead of a random API name
    fun addPost(type: String, content: String, imageUrl: String? = null, onDone: () -> Unit) {
        if (content.isBlank()) return

        viewModelScope.launch {
            val id = withContext(Dispatchers.IO) {
                db.postDao().insertPost(
                    Post(
                        userId = currentUser,
                        content = content,
                        type = type,
                        imageUrl = imageUrl
                    )
                ).toInt()
            }

            // Update local list so UI refreshes immediately
            posts = listOf(
                Post(id, currentUser, content, type, imageUrl)
            ) + posts

            onDone()
        }
    }

    // Gets all responses for a specific post
    suspend fun getResponses(postId: Int): List<Response> {
        return withContext(Dispatchers.IO) {
            db.postDao().getResponsesForPost(postId)
        }
    }

    // Adds a response to a post using the current user
    fun addResponse(postId: Int, content: String, onDone: () -> Unit) {
        if (content.isBlank()) return

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                db.postDao().insertResponse(
                    Response(
                        postId = postId,
                        userId = currentUser,
                        content = content
                    )
                )
            }
            onDone()
        }
    }
    // Holds the list of repos
    private val _repos = mutableListOf(
        Repo("my-portfolio", "Personal portfolio website", "https://github.com"),
        Repo("android-app", "SER210 Final Project client app", "https://github.com")
    )
    var repos: List<Repo> = _repos.toList()
        private set

    fun addRepo(name: String, description: String, githubUrl: String) {
        if (name.isBlank()) return
        _repos.add(Repo(name.trim(), description.trim(), githubUrl.trim().ifEmpty { "https://github.com" }))
        repos = _repos.toList()
    }
}