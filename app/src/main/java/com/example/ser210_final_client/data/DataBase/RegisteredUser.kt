package com.example.ser210_final_client.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registered_users")
data class RegisteredUser(
    @PrimaryKey val username: String,
    val passwordHash: String
)
