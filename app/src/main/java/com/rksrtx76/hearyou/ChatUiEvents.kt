package com.rksrtx76.hearyou

sealed class ChatUiEvents {
    data class UpdatePrompt(val newPrompt : String) : ChatUiEvents()
    data class SendPrompt(val prompt : String) : ChatUiEvents()
}