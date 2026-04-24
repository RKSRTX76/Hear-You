package com.rksrtx76.hearyou.data.model

data class VoiceResponse(
    val voices : List<VoiceInfo>? = null
)

data class VoiceInfo(
    val voiceId : String,
    val name : String,
    val previewUrl : String? = null,
    val labels : Map<String, String>? = null
)