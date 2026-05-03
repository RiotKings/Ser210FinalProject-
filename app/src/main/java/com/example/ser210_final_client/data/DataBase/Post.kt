package com.example.ser210_final_client.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val userId: String,
    val content: String,
    val type: String,
    val imageUrl: String? = null
)