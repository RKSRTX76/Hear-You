package com.rksrtx76.hearyou.data

data class ChatState(
    val prompt : String = "",   // User speech converted to text
    val response : String = "",     // Gemini AI response
    val isLoading : Boolean  = false    // To track when waiting for response from Gemini AI
)
