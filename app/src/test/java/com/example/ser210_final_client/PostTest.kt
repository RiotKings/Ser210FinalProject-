package com.example.ser210_final_client

import com.example.ser210_final_client.data.database.Post
import org.junit.Assert.*
import org.junit.Test

class PostTest {

    // Test that Post object is created correctly
    @Test
    fun post_createdWithCorrectValues() {
        val post = Post(
            id = 0,
            userId = "user123",
            content = "println(\"Hello\")",
            type = "code",
            imageUrl = null
        )

        assertEquals("user123", post.userId)
        assertEquals("println(\"Hello\")", post.content)
        assertEquals("code", post.type)
    }

    // Test that content is not blank
    @Test
    fun post_contentIsNotBlank() {
        val post = Post(0, "user1", "some code", "code", null)
        assertTrue(post.content.isNotBlank())
    }

    // Test that empty content should not be allowed
    @Test
    fun post_emptyContentShouldNotBeAdded() {
        val content = ""
        assertFalse(content.isNotBlank())
    }

    // Test that only "code" type is used for code screen
    @Test
    fun post_typeIsCode() {
        val post = Post(0, "user1", "test", "code", null)
        assertEquals("code", post.type)
    }

    // Test that list increases after adding a post
    @Test
    fun postList_increasesAfterAdding() {
        val posts = mutableListOf(
            Post(0, "u1", "c1", "code", null),
            Post(0, "u2", "c2", "code", null)
        )

        posts.add(Post(0, "u3", "c3", "code", null))

        assertEquals(3, posts.size)
    }
}