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

class Q_AScreen : Fragment() {
    private data class QuestionPost(
        val id: Int,
        val username: String,
        val question: String,
        val responses: MutableList<String> = mutableListOf(),
        var responsesExpanded: Boolean = false
    )

    private val questionPosts = mutableListOf<QuestionPost>()
    private val apiUsernames = mutableListOf<String>()
    private val fallbackUser = "user_001"

    private lateinit var postQuestionButton: Button
    private lateinit var postQuestionNowButton: Button
    private lateinit var cancelQuestionButton: Button
    private lateinit var questionInput: EditText
    private lateinit var composerContainer: LinearLayout
    private lateinit var feedContainer: LinearLayout

    private val db by lazy {
        Room.databaseBuilder(
            requireContext().applicationContext,
            AppDatabase::class.java,
            "code_gram_db"
        ).build()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_question, container, false)
        postQuestionButton = view.findViewById(R.id.postQuestionButton)
        postQuestionNowButton = view.findViewById(R.id.postQuestionNowButton)
        cancelQuestionButton = view.findViewById(R.id.cancelQuestionButton)
        questionInput = view.findViewById(R.id.questionInput)
        composerContainer = view.findViewById(R.id.postQuestionComposerContainer)
        feedContainer = view.findViewById(R.id.questionFeedContainer)

        fetchApiUsernames()
        loadQuestionsFromDatabase()

        postQuestionButton.setOnClickListener {
            composerContainer.visibility = View.VISIBLE
        }

        cancelQuestionButton.setOnClickListener {
            clearComposer()
            composerContainer.visibility = View.GONE
        }

        postQuestionNowButton.setOnClickListener {
            val text = questionInput.text.toString().trim()
            if (text.isBlank()) return@setOnClickListener

            val username = if (apiUsernames.isNotEmpty()) {
                apiUsernames[Random.nextInt(apiUsernames.size)]
            } else {
                fallbackUser
            }

            saveQuestionPost(username, text)
        }

        return view
    }

    private fun clearComposer() {
        questionInput.setText("")
    }

    private fun fetchApiUsernames() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) { ApiInterface.create().getUsers() }
                val fetched = response.body()?.results?.map { it.login.username }?.filter { it.isNotBlank() } ?: emptyList()
                apiUsernames.clear()
                apiUsernames.addAll(fetched)
            } catch (_: Exception) {
                apiUsernames.clear()
            }
        }
    }

    private fun loadQuestionsFromDatabase() {
        viewLifecycleOwner.lifecycleScope.launch {
            val loaded = withContext(Dispatchers.IO) {
                val posts = db.postDao().getAllPosts()
                    .filter { it.type == "question" }
                    .sortedByDescending { it.id }

                posts.map { post ->
                    val responses = db.postDao().getResponsesForPost(post.id).map { it.content }.toMutableList()
                    QuestionPost(
                        id = post.id,
                        username = post.userId,
                        question = post.content,
                        responses = responses
                    )
                }
            }

            questionPosts.clear()
            questionPosts.addAll(loaded)
            renderQuestions()
        }
    }

    private fun saveQuestionPost(username: String, question: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val insertedId = withContext(Dispatchers.IO) {
                db.postDao().insertPost(
                    Post(
                        userId = username,
                        content = question,
                        type = "question",
                        imageUrl = null
                    )
                ).toInt()
            }

            questionPosts.add(0, QuestionPost(id = insertedId, username = username, question = question))
            clearComposer()
            composerContainer.visibility = View.GONE
            renderQuestions()
        }
    }

    private fun renderQuestions() {
        feedContainer.removeAllViews()
        for (post in questionPosts) {
            val row = layoutInflater.inflate(R.layout.item_question_post, feedContainer, false)
            val usernameText = row.findViewById<TextView>(R.id.questionUsernameText)
            val questionText = row.findViewById<TextView>(R.id.questionContentText)
            val toggleButton = row.findViewById<Button>(R.id.toggleQuestionResponsesButton)
            val responsesSection = row.findViewById<LinearLayout>(R.id.questionResponsesSection)
            val responsesList = row.findViewById<LinearLayout>(R.id.questionResponsesListContainer)
            val responseInput = row.findViewById<EditText>(R.id.questionResponseInput)
            val sendButton = row.findViewById<Button>(R.id.sendQuestionResponseButton)

            usernameText.text = post.username
            questionText.text = post.question
            responsesSection.visibility = if (post.responsesExpanded) View.VISIBLE else View.GONE
            toggleButton.text = if (post.responsesExpanded) "Hide Responses ▲" else "See Responses ▼"
            renderResponses(responsesList, post.responses)

            toggleButton.setOnClickListener {
                post.responsesExpanded = !post.responsesExpanded
                renderQuestions()
            }

            sendButton.setOnClickListener {
                val text = responseInput.text.toString().trim()
                if (text.isBlank()) return@setOnClickListener

                viewLifecycleOwner.lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        db.postDao().insertResponse(
                            Response(
                                postId = post.id,
                                userId = fallbackUser,
                                content = text
                            )
                        )
                    }
                    post.responses.add(text)
                    responseInput.setText("")
                    renderQuestions()
                }
            }

            feedContainer.addView(row)
        }
    }

    private fun renderResponses(container: LinearLayout, responses: List<String>) {
        container.removeAllViews()
        for (response in responses) {
            val responseText = TextView(requireContext())
            responseText.text = response
            responseText.textSize = 14f
            responseText.setTextColor(resources.getColor(R.color.primary_text, null))
            responseText.setPadding(6, 4, 6, 4)
            container.addView(responseText)
        }
    }
}