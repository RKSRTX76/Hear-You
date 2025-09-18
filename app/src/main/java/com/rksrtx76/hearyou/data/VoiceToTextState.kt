package com.rksrtx76.hearyou.data

data class VoiceToTextState(
    val isSpeaking : Boolean = false,
    val spokenText : String = "",
    val error : String? = null
)
