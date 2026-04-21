package com.example.ser210_final_client.data.database

import com.example.ser210_final_client.data.database.Post
import com.example.ser210_final_client.data.database.PostDao
import com.example.ser210_final_client.data.database.PostRepository
import com.example.ser210_final_client.data.database.Response

class OfflinePostRepository(
    private val postDao: PostDao
) : PostRepository {

    override suspend fun addPost(post: Post) {
        postDao.insertPost(post)
    }

    override suspend fun getPosts(): List<Post> {
        return postDao.getAllPosts()
    }

    override suspend fun addResponse(response: Response) {
        postDao.insertResponse(response)
    }

    override suspend fun getResponses(postId: Int): List<Response> {
        return postDao.getResponsesForPost(postId)
    }
}