package com.example.ser210_final_client.screens

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.ser210_final_client.R
import com.example.ser210_final_client.data.database.AppDatabase
import com.example.ser210_final_client.data.database.Post
import com.example.ser210_final_client.data.database.Response
import com.example.ser210_final_client.util.SessionPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MemesScreen : Fragment() {
    private data class MemePost(
        val id: Int,
        val username: String,
        val memeFileName: String,
        val caption: String,
        val responses: MutableList<String> = mutableListOf(),
        var responsesExpanded: Boolean = false
    )

    private var nextPostId = 1
    private val memePosts = mutableListOf<MemePost>()

    private lateinit var memeSpinner: Spinner
    private lateinit var captionInput: EditText
    private lateinit var postButton: Button
    private lateinit var postNowButton: Button
    private lateinit var cancelPostButton: Button
    private lateinit var postComposerContainer: LinearLayout
    private lateinit var feedContainer: LinearLayout
    private var memeFileNames: List<String> = emptyList()
    private val db by lazy { AppDatabase.getInstance(requireContext().applicationContext) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_memes, container, false)
        memeSpinner = view.findViewById(R.id.memePickerSpinner)
        captionInput = view.findViewById(R.id.memeCaptionInput)
        postButton = view.findViewById(R.id.postMemeButton)
        postNowButton = view.findViewById(R.id.postNowButton)
        cancelPostButton = view.findViewById(R.id.cancelPostButton)
        postComposerContainer = view.findViewById(R.id.postComposerContainer)
        feedContainer = view.findViewById(R.id.memeFeedContainer)

        memeFileNames = loadMemeFileNames()
        setupMemePicker()
        loadMemesFromDatabase()

        postButton.setOnClickListener {
            postComposerContainer.visibility = View.VISIBLE
        }

        cancelPostButton.setOnClickListener {
            clearComposer()
            postComposerContainer.visibility = View.GONE
        }

        postNowButton.setOnClickListener {
            if (memeFileNames.isEmpty()) {
                Toast.makeText(requireContext(), "No memes found in assets/memes", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedIndex = memeSpinner.selectedItemPosition
            if (selectedIndex < 0 || selectedIndex >= memeFileNames.size) {
                Toast.makeText(requireContext(), "Please choose a meme", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val username = SessionPrefs.displayName(requireContext())

            val newPost = MemePost(
                id = 0,
                username = username,
                memeFileName = memeFileNames[selectedIndex],
                caption = captionInput.text.toString().trim()
            )
            saveMemePost(newPost)
        }

        return view
    }

    private fun loadMemesFromDatabase() {
        viewLifecycleOwner.lifecycleScope.launch {
            val loadedPosts = withContext(Dispatchers.IO) {
                val allPosts = db.postDao().getAllPosts()
                    .filter { it.type == "meme" }
                    .sortedByDescending { it.id }

                allPosts.map { post ->
                    val responses = db.postDao().getResponsesForPost(post.id).map { it.content }.toMutableList()
                    MemePost(
                        id = post.id,
                        username = post.userId,
                        memeFileName = post.imageUrl ?: "",
                        caption = post.content,
                        responses = responses
                    )
                }.filter { it.memeFileName.isNotBlank() }
            }

            memePosts.clear()
            memePosts.addAll(loadedPosts)
            nextPostId = (memePosts.maxOfOrNull { it.id } ?: 0) + 1
            renderPosts()
        }
    }

    private fun saveMemePost(post: MemePost) {
        viewLifecycleOwner.lifecycleScope.launch {
            val insertedId = withContext(Dispatchers.IO) {
                db.postDao().insertPost(
                    Post(
                        userId = post.username,
                        content = post.caption,
                        type = "meme",
                        imageUrl = post.memeFileName
                    )
                ).toInt()
            }

            memePosts.add(
                0,
                post.copy(id = insertedId)
            )
            clearComposer()
            postComposerContainer.visibility = View.GONE
            renderPosts()
        }
    }

    private fun clearComposer() {
        captionInput.setText("")
        if (memeFileNames.isNotEmpty()) {
            memeSpinner.setSelection(0)
        }
    }

    private fun loadMemeFileNames(): List<String> {
        return try {
            requireContext().assets.list("memes")
                ?.filter { it.endsWith(".png") || it.endsWith(".jpg") || it.endsWith(".jpeg") || it.endsWith(".webp") }
                ?.sorted()
                ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun setupMemePicker() {
        val entries = if (memeFileNames.isEmpty()) listOf("No memes available") else memeFileNames
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, entries)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        memeSpinner.adapter = adapter
    }

    private fun renderPosts() {
        feedContainer.removeAllViews()
        for (post in memePosts) {
            val postView = layoutInflater.inflate(R.layout.item_meme_post, feedContainer, false)
            val usernameText = postView.findViewById<TextView>(R.id.postUsernameText)
            val imageView = postView.findViewById<ImageView>(R.id.postImageView)
            val captionText = postView.findViewById<TextView>(R.id.postCaptionText)
            val toggleButton = postView.findViewById<Button>(R.id.toggleResponsesButton)
            val responsesSection = postView.findViewById<LinearLayout>(R.id.responsesSection)
            val responsesList = postView.findViewById<LinearLayout>(R.id.responsesListContainer)
            val responseInput = postView.findViewById<EditText>(R.id.responseInput)
            val sendButton = postView.findViewById<Button>(R.id.sendResponseButton)

            usernameText.text = post.username
            captionText.text = post.caption
            captionText.visibility = if (post.caption.isBlank()) View.GONE else View.VISIBLE

            try {
                val memeBitmap = requireContext().assets.open("memes/${post.memeFileName}").use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
                imageView.setImageBitmap(memeBitmap)
            } catch (_: Exception) {
                imageView.setImageResource(R.drawable.dashboard_picture)
            }

            responsesSection.visibility = if (post.responsesExpanded) View.VISIBLE else View.GONE
            toggleButton.text = if (post.responsesExpanded) "Hide Responses ▲" else "See Responses ▼"
            renderResponsesList(responsesList, post.responses)

            toggleButton.setOnClickListener {
                post.responsesExpanded = !post.responsesExpanded
                renderPosts()
            }

            sendButton.setOnClickListener {
                val text = responseInput.text.toString()
                if (text.isBlank()) return@setOnClickListener
                viewLifecycleOwner.lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        db.postDao().insertResponse(
                            Response(
                                postId = post.id,
                                userId = SessionPrefs.displayName(requireContext()),
                                content = text
                            )
                        )
                    }
                    post.responses.add(text)
                    responseInput.setText("")
                    renderPosts()
                }
            }

            feedContainer.addView(postView)
        }
    }

    private fun renderResponsesList(container: LinearLayout, responses: List<String>) {
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