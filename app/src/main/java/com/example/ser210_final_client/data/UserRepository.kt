package com.example.ser210_final_client.data

import com.example.ser210_final_client.data.api.ApiInterface
import com.example.ser210_final_client.data.api.ApiUser

object UserRepository {
    private val api = ApiInterface.create()

    suspend fun getUserById(index: Int): ApiUser? {
        return try {
            val response = api.getUsers()
            if (response.isSuccessful) {
                response.body()?.results?.getOrNull(index)
            } else null
        } catch (e: Exception) {
            null
        }
    }
}