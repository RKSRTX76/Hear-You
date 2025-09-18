package com.rksrtx76.hearyou.presentation

import android.app.Application
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.material3.Text
import androidx.lifecycle.AndroidViewModel
import com.rksrtx76.hearyou.data.TextToSpeechState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TextToSpeechViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val context = application

    private val _state = MutableStateFlow(TextToSpeechState())
    val state : StateFlow<TextToSpeechState> = _state.asStateFlow()

    private var tts : TextToSpeech? = null
    private var isTtsReady : Boolean = false

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status : Int) {
        if (status == TextToSpeech.SUCCESS){
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                _state.value = _state.value.copy(error = "Language not supported")
            }else{
                isTtsReady = true
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener(){

                    override fun onStart(utteranceId: String?) {
                        _state.value = _state.value.copy(isSpeaking = true)
                    }
                    override fun onDone(utteranceId : String?) {
                        _state.value = _state.value.copy(isSpeaking = false)
                    }
                    override fun onError(utteranceId: String?) {
                        _state.value = _state.value.copy(
                            isSpeaking = false,
                            error = "Error during speech"
                        )
                    }
                })
            }
        }else{
            _state.value = _state.value.copy(
                error = "TTS Initialization failed"
            )
        }
    }

    fun speak(text : String, languageCode : String = "en-US"){
        if (!isTtsReady){
            _state.value = _state.value.copy(
                error = "TTS not ready yet"
            )
            return
        }

        val locale = Locale.forLanguageTag(languageCode)
        val result = tts?.setLanguage(locale)

        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
            _state.value = _state.value.copy(
                error = "Language not supported"
            )
            return
        }
        _state.value = _state.value.copy(
            isSpeaking = true,
            text = text,
            error = null
        )
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "TTS_UTTERANCE_ID")
    }

    fun stopSpeaking(){
        tts?.stop()
        _state.value = _state.value.copy(isSpeaking = false)
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }

}