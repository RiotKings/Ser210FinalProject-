package com.example.ser210_final_client.data.api
data class ApiUser(
    val name: Name,
    val login: Login,
    val email: String,
    val picture: Picture
)

data class Name(
    val first: String,
    val last: String
)

data class Login(
    val uuid: String,
    val username: String,
    val password: String
)

data class Picture(
    val large: String
)