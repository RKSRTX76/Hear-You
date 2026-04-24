package com.rksrtx76.hearyou.data.repository

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.rksrtx76.hearyou.data.api.ElevenLabsApiService
import com.rksrtx76.hearyou.data.api.GeminiApiService
import com.rksrtx76.hearyou.data.model.ChatMessage
import com.rksrtx76.hearyou.data.model.ConversationLanguage
import com.rksrtx76.hearyou.data.model.ElevenLabsVoice
import com.rksrtx76.hearyou.domain.repository.ConversationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.coroutines.resume

class ConversationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context : Context,
    private val geminiClient : GeminiApiService,
    private val elevenLabsClient : ElevenLabsApiService
) : ConversationRepository {
    private var mediaPlayer : MediaPlayer? = null

    override fun buildSystemPrompt(
        voice : ElevenLabsVoice,
        language: ConversationLanguage
    ) : String{
        val languageInstruction = when(language){
            ConversationLanguage.ENGLISH ->
                "You MUST respond only in English. Keep responses conversational, warm, and concise (2-4 sentences max). " +
                        "Speak naturally like a close friend, not a formal assistant."
            ConversationLanguage.HINDI ->
                "आपको केवल हिंदी में जवाब देना है। जवाब बातचीत जैसे, गर्म और संक्षिप्त रखें (2-4 वाक्य)। " +
                        "एक करीबी दोस्त की तरह बात करें, कोई औपचारिक सहायक नहीं।"
        }
        return """
            You are ${voice.name}, an AI voice companion. Your personality is ${voice.description}.
            $languageInstruction
            
            Guidelines:
            - Remember the entire conversation history provided and refer back to it naturally.
            - Show empathy and genuine interest in the user.
            - Ask follow-up questions occasionally to keep the conversation flowing.
            - Never break character or mention being an AI unless directly asked.
            - Avoid bullet points, markdown, or lists — only flowing natural speech.
            - Keep responses short enough to feel like spoken conversation, not an essay.
        """.trimIndent()
    }

    override suspend fun getAIReply(
        messages : List<ChatMessage>,
        voice : ElevenLabsVoice,
        language: ConversationLanguage
    ) : Result<String>{
        val systemPrompt = buildSystemPrompt(voice, language)
        return geminiClient.chat(messages, systemPrompt)
    }

    override suspend fun speakText(
        text : String,
        voiceId : String,
        onPlaybackComplete : () -> Unit
    ) : Result<Unit>{

        stopPlayback()

        val bytes = elevenLabsClient.textToSpeech(
            voiceId = voiceId,
            text = text
        ).getOrElse { return Result.failure(it) }

        if(bytes.isEmpty()){
            return Result.failure(IllegalStateException("TTS returned empty audio payload"))
        }

        val tempFile : File = withContext(Dispatchers.IO){
            val file = File(context.cacheDir, "tts_${System.currentTimeMillis()}.mp3")
            FileOutputStream(file).use { fos ->
                fos.write(bytes)
                fos.flush()
                try { fos.fd.sync() } catch (_: Exception) { /* best-effort kernel flush */ }
            }
            file
        }

        if (tempFile.length() == 0L) {
            tempFile.delete()
            return Result.failure(IllegalStateException("TTS temp file is empty after write"))
        }

        return withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { cont ->
                val player = MediaPlayer()
                mediaPlayer = player

                // Keep the stream open until after prepare() — closing earlier can
                // invalidate the fd on some OEM kernels before MediaPlayer dups it.
                val fis = FileInputStream(tempFile)

                try {
                    player.setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )

                    player.setDataSource(fis.fd)

                    // *** KEY FIX: synchronous prepare() instead of prepareAsync() ***
                    // For local files this is instant and avoids the binder-thread
                    // callback that breaks MediaPlayer's threading contract.
                    player.prepare()

                    // Safe to close stream now — MediaPlayer has its own fd copy.
                    fis.close()

                    player.setOnCompletionListener { mp ->
                        onPlaybackComplete()
                        tempFile.delete()
                        mp.release()
                        if (mediaPlayer === mp) mediaPlayer = null
                        if (cont.isActive) cont.resume(Result.success(Unit))
                    }

                    player.setOnErrorListener { mp, what, extra ->
                        tempFile.delete()
                        mp.release()
                        if (mediaPlayer === mp) mediaPlayer = null
                        if (cont.isActive) cont.resume(
                            Result.failure(
                                IllegalStateException("MediaPlayer error: what=$what extra=$extra")
                            )
                        )
                        true
                    }

                    cont.invokeOnCancellation {
                        stopPlayback()
                        tempFile.delete()
                    }

                    // Start immediately — we're already prepared.
                    player.start()

                } catch (e: Exception) {
                    runCatching { fis.close() }
                    tempFile.delete()
                    player.release()
                    mediaPlayer = null
                    if (cont.isActive) cont.resume(Result.failure(e))
                }
            }
        }
    }

    override fun stopPlayback(){
        mediaPlayer?.let { Player->
            try {
                Player.stop()
            }catch (_ : IllegalStateException){ }
            try {
                Player.release()
            }catch (_ : Exception) { }
            mediaPlayer = null
        }
    }
}