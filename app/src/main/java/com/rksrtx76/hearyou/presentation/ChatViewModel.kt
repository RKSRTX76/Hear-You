package com.rksrtx76.hearyou.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rksrtx76.hearyou.ChatUiEvents
import com.rksrtx76.hearyou.data.ChatData
import com.rksrtx76.hearyou.data.ChatState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.w3c.dom.CharacterData
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {
    private val _chatState = MutableStateFlow(ChatState())
    val chatState = _chatState.asStateFlow()

    private val conversationHistory = mutableListOf<Pair<String, String>>() // Pair<User, Assistant>


    private var isFirstResponse = true

    init {
        // Send the system prompt without touching UI state
        sendSystemInstruction("Please talk to me like a friend")
    }

    private fun sendSystemInstruction(instruction : String){
        viewModelScope.launch {
            val response = ChatData.getResponse(instruction)
            // Don't update UI if it's the first response
            isFirstResponse = false
        }
    }

    fun onEvent(event : ChatUiEvents){
        when(event){
            is ChatUiEvents.SendPrompt -> {
                if (event.prompt.isNotEmpty()){
                    addPrompt(event.prompt)
                    getResponse(event.prompt)
                }
            }
            is ChatUiEvents.UpdatePrompt -> {
                _chatState.update {
                    it.copy(
                        prompt = event.newPrompt
                    )
                }
            }
        }
    }


    private fun addPrompt(prompt : String){
        _chatState.update {
            it.copy(
                prompt = prompt,
                response = "",
                isLoading = true
            )
        }
    }

    private fun getResponse(prompt: String) {
        viewModelScope.launch {
            // 🧠 Include conversation memory in prompt
            val friendlyPrompt = buildString {
                appendLine("You are a friendly AI friend. Talk naturally and warmly in short, casual sentences (1–5 sentences).")
                appendLine("Keep responses conversational and human-like.")
                appendLine("Here’s our previous conversation:")

                for ((user, bot) in conversationHistory) {
                    appendLine("User: $user")
                    appendLine("You: $bot")
                }

                appendLine("User: $prompt")
            }

            // Get response from ChatData
            val response = ChatData.getResponse(friendlyPrompt)
            val cleanedResponse = cleanMarkdown(response.prompt)

            // Add to memory (with size limit)
            conversationHistory.add(prompt to cleanedResponse)
            if (conversationHistory.size > 15) {
                conversationHistory.removeAt(0) // keep last 15 messages
            }

            _chatState.update {
                it.copy(
                    response = cleanedResponse,
                    isLoading = false
                )
            }
        }
    }


    private fun cleanMarkdown(input: String): String {
        return input
            .replace(Regex("(?m)^#{1,6}\\s*"), "")
            .replace(Regex("(?m)^[-*+]\\s+"), "")
            .replace(Regex("```[\\s\\S]*?```"), "")
            .replace(Regex("`([^`]*)`"), "$1")
            .replace(Regex("\\*{1,3}"), "")
            .replace(Regex("_+"), "")
            .replace(Regex("\\[([^]]+)]\\([^)]*\\)"), "$1")
            .replace(Regex("\\n{2,}"), "\n")
            .trim()
    }
}