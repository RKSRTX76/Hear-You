package com.rksrtx76.hearyou.presentation

import android.app.Application
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rksrtx76.hearyou.BuildConfig
import com.rksrtx76.hearyou.data.TextToSpeechState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

class ElevenLabsTTSViewModel(application: Application) : AndroidViewModel(application) {


    private val context = application
    private val _state = MutableStateFlow(TextToSpeechState())
    val state: StateFlow<TextToSpeechState> = _state.asStateFlow()

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()


    private val _selectedVoice = MutableStateFlow("bella")
    val selectedVoice = _selectedVoice.asStateFlow()

    // Popular voice IDs (you can test and choose)
    val voiceIds = mapOf(
        "bella" to "EXAVITQu4vr4xnSDxMaL",
        "nicole" to "piTKgcLEGmPE4e6mEKli",
        "rachel" to "21m00Tcm4TlvDq8ikWAM",
        "adam" to "pNInz6obpgDQGcFmaJgB",
        "laura" to "FGY2WhTYpPnrIDTdsKH5",
        "charlie" to "IKne3meq5aSn9XLyUdCD",
        "george" to "JBFqnCBsd6RMkjVDRZzb",
    )
    fun setSelectedVoice(voice: String) {
        _selectedVoice.value = voice
        speak("Hi there, how can I help you?", voice)
    }

    private var currentMediaPlayer: MediaPlayer? = null

    fun speak(text: String, voiceName: String = _selectedVoice.value) {
        if (text.isEmpty()) return

        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(
                    isSpeaking = true,
                    text = text,
                    error = null
                )

                val audioData = generateSpeech(text, voiceName)
                if (audioData != null) {
                    playAudio(audioData)
                } else {
                    _state.value = _state.value.copy(
                        isSpeaking = false,
                        error = "Failed to generate speech"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSpeaking = false,
                    error = "Error: ${e.message}"
                )
                Log.e("ElevenLabsTTS", "Error speaking", e)
            }
        }
    }

    private suspend fun generateSpeech(text: String, voiceName: String): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                val voiceId = voiceIds[voiceName] ?: voiceIds["bella"]!!

                val jsonBody = JSONObject().apply {
                    put("text", text)
                    put("model_id", "eleven_turbo_v2_5") // Fastest, most natural
                    put("voice_settings", JSONObject().apply {
                        put("stability", 0.5)
                        put("similarity_boost", 0.75)
                        put("style", 0.5)  // More expressive
                        put("use_speaker_boost", true)
                    })
                }

                val requestBody = RequestBody.create(
                    "application/json".toMediaTypeOrNull(),
                    jsonBody.toString()
                )

                val request = Request.Builder()
                    .url("https://api.elevenlabs.io/v1/text-to-speech/$voiceId")
                    .addHeader("xi-api-key", BuildConfig.ELEVENLABS_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    response.body?.bytes()
                } else {
                    Log.e("ElevenLabsTTS", "API Error: ${response.code} - ${response.message}")
                    null
                }
            } catch (e: Exception) {
                Log.e("ElevenLabsTTS", "Network error", e)
                null
            }
        }
    }

    private fun playAudio(audioData: ByteArray) {
        try {
            // Stop any currently playing audio
            currentMediaPlayer?.release()

            // Create temp file
            val tempFile = File.createTempFile("elevenlabs_", ".mp3", context.cacheDir)
            tempFile.writeBytes(audioData)

            // Play audio
            currentMediaPlayer = MediaPlayer().apply {
                setDataSource(tempFile.absolutePath)
                setOnCompletionListener {
                    _state.value = _state.value.copy(isSpeaking = false)
                    release()
                    tempFile.delete()
                }
                setOnErrorListener { _, _, _ ->
                    _state.value = _state.value.copy(
                        isSpeaking = false,
                        error = "Error playing audio"
                    )
                    true
                }
                prepare()
                start()
            }
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                isSpeaking = false,
                error = "Error playing audio: ${e.message}"
            )
            Log.e("ElevenLabsTTS", "Error playing audio", e)
        }
    }

    fun stopSpeaking() {
        currentMediaPlayer?.stop()
        currentMediaPlayer?.release()
        currentMediaPlayer = null
        _state.value = _state.value.copy(isSpeaking = false)
    }

    override fun onCleared() {
        super.onCleared()
        currentMediaPlayer?.release()
        client.dispatcher.executorService.shutdown()
    }
}