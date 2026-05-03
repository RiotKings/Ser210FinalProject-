package com.example.ser210_final_client.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ser210_final_client.data.database.AppDatabase
import com.example.ser210_final_client.data.database.Post
import com.example.ser210_final_client.data.database.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QaViewModel(private val db: AppDatabase) : ViewModel() {

    fun loadQuestions(onResult: (List<QuestionRow>) -> Unit) {
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) {
                db.postDao().getAllPosts()
                    .filter { it.type == "question" }
                    .sortedByDescending { it.id }
                    .map { post ->
                        val responses = db.postDao().getResponsesForPost(post.id).map { it.content }
                        QuestionRow(post.id, post.userId, post.content, responses)
                    }
            }
            onResult(list)
        }
    }

    fun insertQuestion(userId: String, content: String, onDone: (Int) -> Unit) {
        viewModelScope.launch {
            val id = withContext(Dispatchers.IO) {
                db.postDao().insertPost(
                    Post(0, userId, content, "question", null)
                ).toInt()
            }
            onDone(id)
        }
    }

    fun insertQuestionResponse(postId: Int, userId: String, content: String, onDone: () -> Unit) {
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
