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
import androidx.lifecycle.ViewModelProvider
import com.example.ser210_final_client.R
import com.example.ser210_final_client.data.database.AppDatabase
import com.example.ser210_final_client.model.QaViewModel
import com.example.ser210_final_client.model.QaViewModelFactory
import com.example.ser210_final_client.model.QuestionRow
import com.example.ser210_final_client.util.SessionPrefs

class Q_AScreen : Fragment() {
    private data class QuestionPost(
        val id: Int,
        val username: String,
        val question: String,
        val responses: MutableList<String> = mutableListOf(),
        var responsesExpanded: Boolean = false
    )

    private val questionPosts = mutableListOf<QuestionPost>()

    private lateinit var viewModel: QaViewModel
    private lateinit var postQuestionButton: Button
    private lateinit var postQuestionNowButton: Button
    private lateinit var cancelQuestionButton: Button
    private lateinit var questionInput: EditText
    private lateinit var composerContainer: LinearLayout
    private lateinit var feedContainer: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val db = AppDatabase.getInstance(requireContext().applicationContext)
        viewModel = ViewModelProvider(
            requireActivity(),
            QaViewModelFactory(db)
        )[QaViewModel::class.java]

        val view = inflater.inflate(R.layout.fragment_question, container, false)
        postQuestionButton = view.findViewById(R.id.postQuestionButton)
        postQuestionNowButton = view.findViewById(R.id.postQuestionNowButton)
        cancelQuestionButton = view.findViewById(R.id.cancelQuestionButton)
        questionInput = view.findViewById(R.id.questionInput)
        composerContainer = view.findViewById(R.id.postQuestionComposerContainer)
        feedContainer = view.findViewById(R.id.questionFeedContainer)

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

            val username = SessionPrefs.displayName(requireContext())
            viewModel.insertQuestion(username, text) { insertedId ->
                questionPosts.add(0, QuestionPost(id = insertedId, username = username, question = text))
                clearComposer()
                composerContainer.visibility = View.GONE
                renderQuestions()
            }
        }

        return view
    }

    private fun clearComposer() {
        questionInput.setText("")
    }

    private fun loadQuestionsFromDatabase() {
        viewModel.loadQuestions { rows ->
            questionPosts.clear()
            questionPosts.addAll(rows.map { rowToQuestionPost(it) })
            renderQuestions()
        }
    }

    private fun rowToQuestionPost(row: QuestionRow): QuestionPost {
        return QuestionPost(
            id = row.id,
            username = row.username,
            question = row.question,
            responses = row.responseTexts.toMutableList()
        )
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
                val author = SessionPrefs.displayName(requireContext())
                viewModel.insertQuestionResponse(post.id, author, text) {
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
