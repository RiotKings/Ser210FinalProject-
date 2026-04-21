package com.example.ser210_final_client.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query



@Dao
interface RepoDao {

    @Insert
    suspend fun insertRepo(repo: Repo)

    @Query("SELECT * FROM repos")
    suspend fun getAllRepos(): List<Repo>
}