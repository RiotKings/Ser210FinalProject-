package com.example.ser210_final_client.model

data class MemeRow(
    val id: Int,
    val username: String,
    val memeFileName: String,
    val caption: String,
    val responseTexts: List<String>
)
