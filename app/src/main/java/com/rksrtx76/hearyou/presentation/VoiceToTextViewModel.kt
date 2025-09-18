package com.rksrtx76.hearyou.presentation

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.AndroidViewModel
import com.rksrtx76.hearyou.data.VoiceToTextState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VoiceToTextViewModel(application: Application) : AndroidViewModel(application),
    RecognitionListener {

    // Needed to create SpeechRecognizer
    private val context = application
    private val _state = MutableStateFlow(VoiceToTextState())
    val state : StateFlow<VoiceToTextState> = _state.asStateFlow()

    private val recognizer = SpeechRecognizer.createSpeechRecognizer(context)

    var onResultCallback : ((String) -> Unit)? = null

    fun startListening(languageCode : String = "en-US"){
        // Reset previous state
        _state.value = VoiceToTextState()
        // If not available, then show error
        if(!SpeechRecognizer.isRecognitionAvailable(context)){
            _state.value = _state.value.copy(error = "Speech recognition not available")
            return
        }
        // Prepare speech recognition intent
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
        }
        // Set the listener and start listening
        recognizer.setRecognitionListener(this)
        recognizer.startListening(intent)

        // Update state to show Speaking
        _state.value = _state.value.copy(isSpeaking = true)
    }

    // Stop listening when user is done
    fun stopListening(){
        try {
            recognizer.stopListening()
        }catch (e : Exception){
            recognizer.cancel()
        }
        _state.value = _state.value.copy(isSpeaking = false)
    }

    override fun onBeginningOfSpeech() {

    }

    override fun onBufferReceived(p0: ByteArray?) {

    }

    override fun onEndOfSpeech() {
        _state.value = _state.value.copy(isSpeaking = false)    // Done speaking
    }

    override fun onError(error: Int) {
        if (error != SpeechRecognizer.ERROR_CLIENT){
            _state.value = _state.value.copy(isSpeaking = false, error = "Error code: $error" )
        }
    }

    override fun onEvent(p0: Int, p1: Bundle?) {

    }

    override fun onPartialResults(p0: Bundle?) {

    }

    override fun onReadyForSpeech(params: Bundle?) {
        _state.value = _state.value.copy(error = null)  // Clear previous error
    }

    override fun onResults(results: Bundle?) {
        val spokenText = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
        if(spokenText != null){
            _state.value = _state.value.copy(spokenText = spokenText, isSpeaking = false)

            // Trigger callback
            onResultCallback?.invoke(spokenText)
        }
    }

    override fun onCleared() {
        super.onCleared()
        recognizer.destroy()    // Clean up
    }

    override fun onRmsChanged(p0: Float) {

    }

}