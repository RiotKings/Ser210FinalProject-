package com.example.ser210_final_client

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ser210_final_client.data.database.AppDatabase
import com.example.ser210_final_client.data.database.Post
import com.example.ser210_final_client.data.database.Response
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun teardown() {
        db.close()
    }

    // Test: insert + read Post
    @Test
    fun post_insertAndRead() = runBlocking {
        val post = Post(
            id = 0,
            userId = "user1",
            content = "code",
            type = "code",
            imageUrl = null
        )

        db.postDao().insertPost(post)
        val posts = db.postDao().getAllPosts()

        assertTrue(posts.isNotEmpty())
        assertEquals("user1", posts[0].userId)
    }

    // Test: insert + read Response linked to a Post
    @Test
    fun response_insertAndReadForPost() = runBlocking {
        val postId = db.postDao().insertPost(
            Post(0, "user1", "code", "code", null)
        ).toInt()

        db.postDao().insertResponse(
            Response(
                postId = postId,
                userId = "user2",
                content = "nice"
            )
        )

        val responses = db.postDao().getResponsesForPost(postId)

        assertTrue(responses.isNotEmpty())
        assertEquals("nice", responses[0].content)
        assertEquals(postId, responses[0].postId)
    }
}