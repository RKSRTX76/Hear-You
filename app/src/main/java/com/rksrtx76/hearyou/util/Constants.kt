package com.rksrtx76.hearyou.util

import com.rksrtx76.hearyou.BuildConfig

object Constants {
    const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta"
    const val ELEVENLABS_BASE_URL = "https://api.elevenlabs.io/v1"

    const val GEMINI_MODEL = "gemini-2.5-flash"
    const val ELEVENLABS_MODEL = "eleven_multilingual_v2"

    const val GEMINI_API_KEY = BuildConfig.GEMINI_API_KEY
    const val ELEVENLABS_API_KEY = BuildConfig.ELEVENLABS_API_KEY

    const val ELEVENLABS_TAG = "ElevenLabsApiClient"
}