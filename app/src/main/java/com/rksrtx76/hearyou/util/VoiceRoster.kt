package com.rksrtx76.hearyou.util

import com.rksrtx76.hearyou.data.model.ConversationLanguage
import com.rksrtx76.hearyou.data.model.ElevenLabsVoice

object VoiceRoster {

    val ENGLISH_VOICES = listOf(
        // ── Female ────────────────────────────────────────────────────────────
        ElevenLabsVoice(
            voiceId     = "EXAVITQu4vr4xnSDxMaL",
            name        = "Sarah",
            language    = ConversationLanguage.ENGLISH,
            gender      = "Female",
            description = "Soft & Friendly"
        ),
        ElevenLabsVoice(
            voiceId     = "XrExE9yKIg1WjnnlVkGX",
            name        = "Matilda",
            language    = ConversationLanguage.ENGLISH,
            gender      = "Female",
            description = "Warm & Intimate"
        ),
        ElevenLabsVoice(
            voiceId     = "pFZP5JQG7iQjIQuC4Bku",
            name        = "Grace",
            language    = ConversationLanguage.ENGLISH,
            gender      = "Female",
            description = "Raspy British"
        ),
        ElevenLabsVoice(
            voiceId     = "Xb7hH8MSUJpSbSDYk0k2",
            name        = "Alice",
            language    = ConversationLanguage.ENGLISH,
            gender      = "Female",
            description = "Confident & Clear"
        ),
        // ── Male ──────────────────────────────────────────────────────────────
        ElevenLabsVoice(
            voiceId     = "ErXwobaYiN019PkySvjV",
            name        = "Antoni",
            language    = ConversationLanguage.ENGLISH,
            gender      = "Male",
            description = "Smooth & Warm"
        ),
        ElevenLabsVoice(
            voiceId     = "iP95p4xoKVk53GoZ742B",
            name        = "Chris",
            language    = ConversationLanguage.ENGLISH,
            gender      = "Male",
            description = "Casual & Friendly"
        ),
        ElevenLabsVoice(
            voiceId     = "nPczCjzI2devNBz1zQrb",
            name        = "Brian",
            language    = ConversationLanguage.ENGLISH,
            gender      = "Male",
            description = "Deep & Trustworthy"
        ),
        ElevenLabsVoice(
            voiceId     = "IKne3meq5aSn9XLyUdCD",
            name        = "Charlie",
            language    = ConversationLanguage.ENGLISH,
            gender      = "Male",
            description = "Natural & Relaxed"
        )
    )

    // For Hindi, eleven_multilingual_v2 is used with premade voices.
    // Only voices that produce the most natural Hindi output are included.
    val HINDI_VOICES = listOf(
        ElevenLabsVoice(
            voiceId     = "EXAVITQu4vr4xnSDxMaL",
            name        = "Sarah",
            language    = ConversationLanguage.HINDI,
            gender      = "Female",
            description = "Soft & Friendly"
        ),
        ElevenLabsVoice(
            voiceId     = "XrExE9yKIg1WjnnlVkGX",
            name        = "Matilda",
            language    = ConversationLanguage.HINDI,
            gender      = "Female",
            description = "Warm"
        ),
        ElevenLabsVoice(
            voiceId     = "Xb7hH8MSUJpSbSDYk0k2",
            name        = "Alice",
            language    = ConversationLanguage.HINDI,
            gender      = "Female",
            description = "Confident & Clear"
        ),
        ElevenLabsVoice(
            voiceId = "ErXwobaYiN019PkySvjV",
            name = "Antoni",
            ConversationLanguage.HINDI,
            gender = "Male",
            description = "Smooth & Friendly"
        ),
        ElevenLabsVoice(
            voiceId     = "IKne3meq5aSn9XLyUdCD",
            name        = "Charlie",
            language    = ConversationLanguage.HINDI,
            gender      = "Male",
            description = "Natural & Relaxed"
        )
    )

    fun voicesFor(language: ConversationLanguage) = when (language) {
        ConversationLanguage.ENGLISH -> ENGLISH_VOICES
        ConversationLanguage.HINDI   -> HINDI_VOICES
    }

    fun defaultVoiceFor(language: ConversationLanguage) = voicesFor(language).first()
}