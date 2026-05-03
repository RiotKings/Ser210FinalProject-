package com.example.ser210_final_client.model

data class QuestionRow(
    val id: Int,
    val username: String,
    val question: String,
    val responseTexts: List<String>
)
