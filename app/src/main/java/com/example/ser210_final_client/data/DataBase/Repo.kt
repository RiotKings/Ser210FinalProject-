package com.example.ser210_final_client.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repos")
data class Repo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val userId: String,
    val repoName: String,
    val description: String,
    val url: String
)