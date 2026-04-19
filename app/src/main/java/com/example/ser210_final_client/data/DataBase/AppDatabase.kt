package com.example.ser210_final_client.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Post::class, Response::class, Repo::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
}