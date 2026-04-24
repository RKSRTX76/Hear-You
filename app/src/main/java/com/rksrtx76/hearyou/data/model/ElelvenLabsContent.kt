package com.rksrtx76.hearyou.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ElevenLabsTTSRequest(
    val text : String,
    @SerialName("model_id")
    val modelId : String,
    @SerialName("voice_settings")
    val voiceSettings : ElevenLabsVoiceSettings = ElevenLabsVoiceSettings()
)

@Serializable
data class ElevenLabsVoiceSettings(
    val stability : Float = 0.50f,
    @SerialName("similarity_boost")
    val similarityBoost : Float = 0.75f,
    val style : Float = 0.10f,
    @SerialName("use_speaker_boost")
    val useSpeakerBoost : Boolean = true
)

@Serializable
data class ElevenLabsVoice(
    val voiceId: String,
    val name: String,
    val language: ConversationLanguage,
    val gender: String = "",
    val previewUrl: String? = null,
    val description: String = ""
)