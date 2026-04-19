package com.example.ser210_final_client.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "responses")
data class Response(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val postId: Int,
    val userId: String,
    val content: String
)