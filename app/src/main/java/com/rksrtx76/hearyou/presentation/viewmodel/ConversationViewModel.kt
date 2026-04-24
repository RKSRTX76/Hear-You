package com.rksrtx76.hearyou.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rksrtx76.hearyou.data.model.ChatMessage
import com.rksrtx76.hearyou.data.model.ChatRole
import com.rksrtx76.hearyou.data.model.ConversationLanguage
import com.rksrtx76.hearyou.data.model.ElevenLabsVoice
import com.rksrtx76.hearyou.domain.repository.ConversationRepository
import com.rksrtx76.hearyou.util.SpeechRecognitionManager
import com.rksrtx76.hearyou.util.SpeechResult
import com.rksrtx76.hearyou.util.VoiceRoster
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    @ApplicationContext private val context : Context,
    private val repository: ConversationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConversationUiState())
    val uiState : StateFlow<ConversationUiState> = _uiState.asStateFlow()


    private val speechManager = SpeechRecognitionManager(context)
    private var listeningJob : Job? = null

    fun selectLanguage(language : ConversationLanguage){
        val voices = VoiceRoster.voicesFor(language)
        val defaultVoice = voices.first()
        _uiState.update {
            it.copy(
                selectedLanguage = language,
                availableVoices = voices,
                selectedVoice = defaultVoice,
                isVoiceSelectorExpanded = false
            )
        }
    }

    fun selectVoice(voice : ElevenLabsVoice){
        _uiState.update {
            it.copy(
                selectedVoice = voice,
                isVoiceSelectorExpanded = false
            )
        }
    }

    fun startListening(){
        if(_uiState.value.voiceState !is VoiceState.Idle) return

        repository.stopPlayback()

        _uiState.update {
            it.copy(
                voiceState = VoiceState.Listening,
                currentTranscript = ""
            )
        }

        val languageCode = when(_uiState.value.selectedLanguage){
            ConversationLanguage.ENGLISH -> "en-US"
            ConversationLanguage.HINDI -> "hi-IN"
        }

        listeningJob = viewModelScope.launch {
            speechManager.recognize(languageCode).collect { result ->
                when(result){
                    is SpeechResult.Partial -> {
                        _uiState.update {
                            it.copy(currentTranscript = result.text)
                        }
                    }

                    is SpeechResult.Final -> {
                        if(result.text.isNotBlank()){
                            _uiState.update {
                                it.copy(currentTranscript = result.text)
                            }
                            handleUserSpeech(result.text)
                        }
                        else{
                            _uiState.update {
                                it.copy(voiceState = VoiceState.Idle, currentTranscript = "")
                            }
                        }
                    }
                    is SpeechResult.Error -> {
                        if (result.code == 7) { // ERROR_NO_MATCH
                            _uiState.update { it.copy(voiceState = VoiceState.Idle, currentTranscript = "") }
                        } else {
                            _uiState.update {
                                it.copy(
                                    voiceState     = VoiceState.Error(result.message),
                                    currentTranscript = "",
                                    errorMessage   = result.message
                                )
                            }
                            // Auto-reset after showing error
                            kotlinx.coroutines.delay(2500)
                            _uiState.update { it.copy(voiceState = VoiceState.Idle, errorMessage = null) }
                        }
                    }
                    is SpeechResult.RmsChange -> Unit
                }
            }
        }
    }

    fun stopListening(){
        speechManager.stop()
        listeningJob?.cancel()
        listeningJob = null

        val currentTranscript = _uiState.value.currentTranscript
        if(currentTranscript.isNotBlank()){
            handleUserSpeech(currentTranscript)
        }else{
            _uiState.update {
                it.copy(
                    voiceState = VoiceState.Idle, currentTranscript = ""
                )
            }
        }
    }

    private fun handleUserSpeech(text: String) {
        val userMessage = ChatMessage(
            role = ChatRole.User,
            text = text
        )

        // Capture updated messages list immediately after adding the user turn
        val updatedMessages = _uiState.value.messages + userMessage

        _uiState.update {
            it.copy(
                messages          = updatedMessages,
                voiceState        = VoiceState.Processing,
                currentTranscript = ""
            )
        }

        // Snapshot voice/language from state before launching coroutine
        val voice    = _uiState.value.selectedVoice
        val language = _uiState.value.selectedLanguage

        viewModelScope.launch {
            val result = repository.getAIReply(
                messages = updatedMessages,
                voice    = voice,
                language = language
            )

            result.onSuccess { replyText ->
                val assistantMessage = ChatMessage(
                    role = ChatRole.Assistant,
                    text = replyText,
                    voiceName = voice.name
                )

                _uiState.update {
                    it.copy(
                        messages   = it.messages + assistantMessage,
                        voiceState = VoiceState.Speaking
                    )
                }

                repository.speakText(
                    text               = replyText,
                    voiceId            = voice.voiceId,
                    onPlaybackComplete = {
                        _uiState.update { it.copy(voiceState = VoiceState.Idle) }
                    }
                ).onFailure { err ->
                    _uiState.update {
                        it.copy(
                            voiceState   = VoiceState.Idle,
                            errorMessage = "TTS failed: ${err.message}"
                        )
                    }
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        voiceState   = VoiceState.Error(err.message ?: "Unknown error"),
                        errorMessage = err.message
                    )
                }
                kotlinx.coroutines.delay(2500)
                _uiState.update { it.copy(voiceState = VoiceState.Idle, errorMessage = null) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearConversation() {
        repository.stopPlayback()
        _uiState.update {
            it.copy(
                messages       = emptyList(),
                voiceState     = VoiceState.Idle,
                currentTranscript = "",
                errorMessage   = null
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.stopPlayback()
        speechManager.destroy()
    }
}

