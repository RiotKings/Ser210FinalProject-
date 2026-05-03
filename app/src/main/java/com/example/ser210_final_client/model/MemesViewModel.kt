package com.example.ser210_final_client.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ser210_final_client.data.database.AppDatabase
import com.example.ser210_final_client.data.database.Post
import com.example.ser210_final_client.data.database.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MemesViewModel(private val db: AppDatabase) : ViewModel() {

    fun loadMemes(onResult: (List<MemeRow>) -> Unit) {
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) {
                db.postDao().getAllPosts()
                    .filter { it.type == "meme" }
                    .sortedByDescending { it.id }
                    .map { post ->
                        val responses = db.postDao().getResponsesForPost(post.id).map { it.content }
                        MemeRow(
                            post.id,
                            post.userId,
                            post.imageUrl ?: "",
                            post.content,
                            responses
                        )
                    }
                    .filter { it.memeFileName.isNotBlank() }
            }
            onResult(list)
        }
    }

    fun insertMeme(userId: String, caption: String, fileName: String, onDone: (Int) -> Unit) {
        viewModelScope.launch {
            val id = withContext(Dispatchers.IO) {
                db.postDao().insertPost(
                    Post(0, userId, caption, "meme", fileName)
                ).toInt()
            }
            onDone(id)
        }
    }

    fun insertMemeResponse(postId: Int, userId: String, content: String, onDone: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                db.postDao().insertResponse(
                    Response(postId = postId, userId = userId, content = content)
                )
            }
            onDone()
        }
    }
}
