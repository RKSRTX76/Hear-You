package com.rksrtx76.hearyou.data

data class TextToSpeechState(
    val isSpeaking : Boolean = false,
    val text : String = "",
    val error : String? = null
)
