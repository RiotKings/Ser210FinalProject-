package com.example.ser210_final_client.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PostDao {

    @Insert
    suspend fun insertPost(post: Post): Long

    @Query("SELECT * FROM posts")
    suspend fun getAllPosts(): List<Post>

    @Insert
    suspend fun insertResponse(response: Response)

    @Query("SELECT * FROM responses WHERE postId = :postId")
    suspend fun getResponsesForPost(postId: Int): List<Response>
}