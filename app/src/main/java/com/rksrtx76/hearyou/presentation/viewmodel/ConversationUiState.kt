package com.rksrtx76.hearyou.presentation.viewmodel

import com.rksrtx76.hearyou.data.model.ChatMessage
import com.rksrtx76.hearyou.data.model.ConversationLanguage
import com.rksrtx76.hearyou.data.model.ElevenLabsVoice
import com.rksrtx76.hearyou.util.VoiceRoster

data class ConversationUiState(
    val messages: List<ChatMessage> = emptyList(),
    val voiceState: VoiceState = VoiceState.Idle,
    val selectedLanguage: ConversationLanguage = ConversationLanguage.ENGLISH,
    val selectedVoice: ElevenLabsVoice = VoiceRoster.defaultVoiceFor(ConversationLanguage.ENGLISH),
    val availableVoices: List<ElevenLabsVoice> = VoiceRoster.ENGLISH_VOICES,
    val isVoiceSelectorExpanded: Boolean = false,
    val currentTranscript: String = "",
    val errorMessage: String? = null
)

sealed class VoiceState {
    object Idle          : VoiceState()
    object Listening     : VoiceState()
    object Processing    : VoiceState()
    object Speaking      : VoiceState()
    data class Error(val message: String) : VoiceState()
}