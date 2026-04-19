package com.example.ser210_final_client.data.database

interface PostRepository {

    suspend fun addPost(post: Post)

    suspend fun getPosts(): List<Post>

    suspend fun addResponse(response: Response)

    suspend fun getResponses(postId: Int): List<Response>
}