package com.example.ser210_final_client.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserAccountDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: RegisteredUser): Long

    @Query("SELECT * FROM registered_users WHERE LOWER(username) = LOWER(:username) LIMIT 1")
    suspend fun findByUsernameIgnoreCase(username: String): RegisteredUser?
}
