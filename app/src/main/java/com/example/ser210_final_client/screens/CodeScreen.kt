package com.example.ser210_final_client.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.ser210_final_client.R
import com.example.ser210_final_client.data.api.ApiInterface
import com.example.ser210_final_client.data.database.AppDatabase
import com.example.ser210_final_client.data.database.Post
import com.example.ser210_final_client.data.database.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class CodeScreen : Fragment() {

    private lateinit var container: LinearLayout
    private lateinit var input: EditText
    private lateinit var postBtn: Button

    private val db by lazy {
        Room.databaseBuilder(
            requireContext().applicationContext,
            AppDatabase::class.java,
            "code_gram_db"
        ).build()
    }

    private val codePosts = mutableListOf<Post>()
    private val usernames = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        containerView: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_code, containerView, false)

        container = view.findViewById(R.id.codeContainer)
        input = view.findViewById(R.id.codeInput)
        postBtn = view.findViewById(R.id.postCodeBtn)

        loadUsers()
        loadPosts()

        postBtn.setOnClickListener {
            val text = input.text.toString()
            if (text.isBlank()) return@setOnClickListener

            val username = if (usernames.isNotEmpty()) {
                usernames[Random.nextInt(usernames.size)]
            } else {
                "user_001"
            }

            lifecycleScope.launch {
                val id = withContext(Dispatchers.IO) {
                    db.postDao().insertPost(
                        Post(
                            userId = username,
                            content = text,
                            type = "code",
                            imageUrl = null
                        )
                    ).toInt()
                }

                val newPost = Post(id, username, text, "code", null)
                codePosts.add(0, newPost)

                renderPosts()
                input.setText("")
            }
        }

        return view
    }

    private fun loadUsers() {
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    ApiInterface.create().getUsers().body()?.results ?: emptyList()
                } catch (_: Exception) {
                    emptyList()
                }
            }
            usernames.clear()
            usernames.addAll(result.map { it.login.username })
        }
    }

    private fun loadPosts() {
        lifecycleScope.launch {
            val posts = withContext(Dispatchers.IO) {
                db.postDao().getAllPosts()
                    .filter { it.type == "code" }
                    .sortedByDescending { it.id }
            }
            codePosts.clear()
            codePosts.addAll(posts)
            renderPosts()
        }
    }

    private fun renderPosts() {
        container.removeAllViews()

        for (post in codePosts) {
            val postView = layoutInflater.inflate(R.layout.item_code_post, container, false)

            val userText = postView.findViewById<TextView>(R.id.codeUser)
            val contentText = postView.findViewById<TextView>(R.id.codeContent)
            val toggleBtn = postView.findViewById<Button>(R.id.toggleResponsesBtn)
            val responseContainer = postView.findViewById<LinearLayout>(R.id.responseContainer)
            val responseInput = postView.findViewById<EditText>(R.id.responseInput)
            val sendBtn = postView.findViewById<Button>(R.id.sendResponseBtn)

            userText.text = post.userId
            contentText.text = post.content

            toggleBtn.setOnClickListener {
                if (responseContainer.visibility == View.GONE) {
                    responseContainer.visibility = View.VISIBLE
                    loadResponses(post.id, responseContainer)
                } else {
                    responseContainer.visibility = View.GONE
                }
            }

            sendBtn.setOnClickListener {
                val text = responseInput.text.toString()
                if (text.isBlank()) return@setOnClickListener

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        db.postDao().insertResponse(
                            Response(
                                postId = post.id,
                                userId = "user_001",
                                content = text
                            )
                        )
                    }
                    responseInput.setText("")
                    loadResponses(post.id, responseContainer)
                }
            }

            container.addView(postView)
        }
    }

    private fun loadResponses(postId: Int, container: LinearLayout) {
        lifecycleScope.launch {
            val responses = withContext(Dispatchers.IO) {
                db.postDao().getResponsesForPost(postId)
            }

            container.removeAllViews()

            for (res in responses) {
                val tv = TextView(requireContext())
                tv.text = res.content
                tv.setTextColor(resources.getColor(android.R.color.darker_gray, null))
                container.addView(tv)
            }
        }
    }
}