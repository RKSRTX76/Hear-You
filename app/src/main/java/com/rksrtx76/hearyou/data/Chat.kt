package com.rksrtx76.hearyou.data

data class Chat(
    val prompt : String,
    val isFromUser : Boolean,
    val isLoading : Boolean = false,
)