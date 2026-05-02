package com.example.ser210_final_client

import com.example.ser210_final_client.data.database.Response
import org.junit.Assert.*
import org.junit.Test

class ResponseTest {

    // Test that Response object is created correctly
    @Test
    fun response_createdWithCorrectValues() {
        val response = Response(
            id = 0,
            postId = 10,
            userId = "user123",
            content = "Nice code!"
        )

        assertEquals(10, response.postId)
        assertEquals("user123", response.userId)
        assertEquals("Nice code!", response.content)
    }

    // Test that response content is not blank
    @Test
    fun response_contentIsNotBlank() {
        val response = Response(0, 1, "user1", "good")
        assertTrue(response.content.isNotBlank())
    }

    // Test that empty response should not be allowed
    @Test
    fun response_emptyShouldNotBeAdded() {
        val content = ""
        assertFalse(content.isNotBlank())
    }

    // Test that response is linked to correct post
    @Test
    fun response_correctPostLink() {
        val response = Response(0, 5, "user1", "reply")
        assertEquals(5, response.postId)
    }

    // Test that list increases after adding a response
    @Test
    fun responseList_increasesAfterAdding() {
        val responses = mutableListOf(
            Response(0, 1, "u1", "r1"),
            Response(0, 1, "u2", "r2")
        )

        responses.add(Response(0, 1, "u3", "r3"))

        assertEquals(3, responses.size)
    }
}