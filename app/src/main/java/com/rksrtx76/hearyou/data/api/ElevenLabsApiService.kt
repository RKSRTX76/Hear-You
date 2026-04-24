package com.rksrtx76.hearyou.data.api

import android.util.Log
import com.rksrtx76.hearyou.data.model.ElevenLabsTTSRequest
import com.rksrtx76.hearyou.data.model.ElevenLabsVoiceSettings
import com.rksrtx76.hearyou.util.Constants.ELEVENLABS_API_KEY
import com.rksrtx76.hearyou.util.Constants.ELEVENLABS_BASE_URL
import com.rksrtx76.hearyou.util.Constants.ELEVENLABS_MODEL
import com.rksrtx76.hearyou.util.Constants.ELEVENLABS_TAG
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readBytes
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ElevenLabsApiService @Inject constructor(
    private val client : HttpClient
) {
    suspend fun textToSpeech(
        voiceId : String,
        text : String,
        voiceSettings : ElevenLabsVoiceSettings = ElevenLabsVoiceSettings()
    ) : Result<ByteArray> = runCatching {
        val request = ElevenLabsTTSRequest(
            text = text,
            modelId = ELEVENLABS_MODEL,
            voiceSettings = voiceSettings
        )

        val response : HttpResponse = client.post("$ELEVENLABS_BASE_URL/text-to-speech/$voiceId"){
            header("xi-api-key", ELEVENLABS_API_KEY)
            header("Accept", "audio/mpeg")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

        if(!response.status.isSuccess()){
            val errorBody = try{
                response.readBytes().toString(Charsets.UTF_8)
            }catch (e : Exception){

            }
            Log.e(ELEVENLABS_TAG, "ElevenLabs TTS HTTP ${response.status.value}: $errorBody")
            throw IllegalStateException(
                "ElevenLabs TTS failed — HTTP ${response.status.value} " +
                        "(${response.status.description}): $errorBody"
            )
        }

        val contentType = response.contentType()
        if (contentType != null && contentType.contentType != "audio") {
            val body = try { response.readBytes().toString(Charsets.UTF_8) } catch (_: Exception) { "<unreadable>" }
            android.util.Log.e(ELEVENLABS_TAG, "ElevenLabs returned non-audio content ($contentType): $body")
            throw IllegalStateException(
                "ElevenLabs returned unexpected content type '$contentType' instead of audio. Body: $body"
            )
        }

        val bytes = response.readBytes()

        // ── Validate we actually got audio bytes ──────────────────────────────
        if (bytes.isEmpty()) {
            throw IllegalStateException("ElevenLabs returned empty audio body")
        }

        // MP3 files start with 0xFF 0xFB/0xF3/0xF2 (MPEG sync) or ID3 tag (0x49 0x44 0x33)
        val looksLikeMp3 = bytes.size >= 3 &&
                (bytes[0] == 0xFF.toByte() || (bytes[0] == 0x49.toByte() && bytes[1] == 0x44.toByte() && bytes[2] == 0x33.toByte()))
        if (!looksLikeMp3) {
            val preview = bytes.take(200).toByteArray().toString(Charsets.UTF_8)
            Log.e(ELEVENLABS_TAG, "Audio bytes don't look like MP3. First 200 bytes as text: $preview")
            throw IllegalStateException(
                "ElevenLabs response doesn't appear to be valid MP3 audio. " +
                        "Likely an error JSON was returned. Preview: $preview"
            )
        }

        Log.d(ELEVENLABS_TAG, "TTS success — ${bytes.size} bytes for voiceId=$voiceId")
        bytes
    }

}