package com.rksrtx76.hearyou.data.model

import java.util.UUID

data class ChatMessage(
    val id : String = UUID.randomUUID().toString(), // generate unique id to identify 3f9c2b6e-8f5d-4c1a-9c2f-1a2b3c4d5e6f
    val role : ChatRole,
    val text : String,
    val timeStamp : Long = System.currentTimeMillis(),
    val voiceName : String? = null
)


sealed class ChatRole{
    object User : ChatRole()
    object Assistant : ChatRole()
}
