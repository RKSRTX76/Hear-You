package com.rksrtx76.hearyou.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

sealed class SpeechResult {
    data class Partial(val text: String) : SpeechResult()
    data class Final(val text: String)   : SpeechResult()
    data class Error(val code: Int, val message: String) : SpeechResult()
    object RmsChange : SpeechResult()
}

class SpeechRecognitionManager(private val context: Context) {

    private var recognizer: SpeechRecognizer? = null

    fun recognize(languageCode: String): Flow<SpeechResult> = callbackFlow {
        recognizer?.destroy()
        recognizer = SpeechRecognizer.createSpeechRecognizer(context)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languageCode)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1000L)
        }

        recognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {
                trySend(SpeechResult.RmsChange)
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val partial = partialResults
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull() ?: return
                trySend(SpeechResult.Partial(partial))
            }

            override fun onResults(results: Bundle?) {
                val text = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull() ?: ""
                trySend(SpeechResult.Final(text))
                channel.close()
            }

            override fun onError(error: Int) {
                val msg = when (error) {
                    SpeechRecognizer.ERROR_AUDIO             -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT            -> "Client side error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    SpeechRecognizer.ERROR_NETWORK           -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT   -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH          -> "No speech detected"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY   -> "Recognizer busy"
                    SpeechRecognizer.ERROR_SERVER            -> "Server error"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT    -> "No speech input"
                    else -> "Unknown error ($error)"
                }
                trySend(SpeechResult.Error(error, msg))
                channel.close()
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        recognizer?.startListening(intent)

        awaitClose {
            recognizer?.stopListening()
            recognizer?.destroy()
            recognizer = null
        }
    }

    fun stop() {
        recognizer?.stopListening()
    }

    fun destroy() {
        recognizer?.destroy()
        recognizer = null
    }
}