package com.rksrtx76.hearyou.domain.repository

import com.rksrtx76.hearyou.data.model.ChatMessage
import com.rksrtx76.hearyou.data.model.ConversationLanguage
import com.rksrtx76.hearyou.data.model.ElevenLabsVoice

interface ConversationRepository {
    fun buildSystemPrompt(voice : ElevenLabsVoice, language: ConversationLanguage) : String
    suspend fun getAIReply(messages : List<ChatMessage>, voice : ElevenLabsVoice, language: ConversationLanguage) : Result<String>

    suspend fun speakText(text : String, voiceId : String, onPlaybackComplete : () -> Unit) : Result<Unit>

    fun stopPlayback()
}