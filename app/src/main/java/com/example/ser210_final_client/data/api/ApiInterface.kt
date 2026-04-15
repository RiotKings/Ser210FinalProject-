package com.example.ser210_final_client.data.api


import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiInterface {

    @GET("api/?results=10&seed=myapp&inc=name,login,email,picture")
    suspend fun getUsers(): Response<UserResponse>

    companion object {

        private const val BASE_URL = "https://randomuser.me/"

        fun create(): ApiInterface {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiInterface::class.java)
        }
    }
}