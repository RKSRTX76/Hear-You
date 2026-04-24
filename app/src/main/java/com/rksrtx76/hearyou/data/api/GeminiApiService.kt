package com.rksrtx76.hearyou.data.api

import com.rksrtx76.hearyou.data.model.ChatMessage
import com.rksrtx76.hearyou.data.model.ChatRole
import com.rksrtx76.hearyou.data.model.GeminiContent
import com.rksrtx76.hearyou.data.model.GeminiPart
import com.rksrtx76.hearyou.data.model.GeminiRequest
import com.rksrtx76.hearyou.data.model.GeminiResponse
import com.rksrtx76.hearyou.util.Constants.GEMINI_API_KEY
import com.rksrtx76.hearyou.util.Constants.GEMINI_BASE_URL
import com.rksrtx76.hearyou.util.Constants.GEMINI_MODEL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiApiService @Inject constructor(
    private val client : HttpClient
){
    suspend fun chat(
        conversationHistory : List<ChatMessage>,
        systemPrompt : String
    ) : Result<String> = runCatching {
        // build alternating chat for user and model
        val contents = buildAlternatingContents(conversationHistory)

        val systemInstruction = GeminiContent(
            role = "user",
            parts = listOf(GeminiPart(systemPrompt))
        )

        val request = GeminiRequest(
            contents = contents,
            systemInstruction = systemInstruction,
        )

        val response : GeminiResponse = client.post(
            "$GEMINI_BASE_URL/models/$GEMINI_MODEL:generateContent"
        ){
            parameter("key", GEMINI_API_KEY)
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

        response.error?.let { error ->
            throw IllegalStateException("Gemini error ${error.code}: ${error.message}")
        }

        response.candidates
            ?.firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text
            ?: throw IllegalStateException("Empty response from Gemini")
    }
}



private fun buildAlternatingContents(
    messages : List<ChatMessage>
) : List<GeminiContent>{
    val result = mutableListOf<GeminiContent>()
    var lastRole = ""

    for(message in messages){
        val role = if(message.role is ChatRole.User) "user" else "model"
        if(role == lastRole){
            // Merge into the last entry to avoid consecutive same role violation
            val last = result.last()
            result[result.size - 1] = last.copy(
                // apply changes
                parts = last.parts + GeminiPart("\n" + message.text)
            )
        }else{
            result.add(
                GeminiContent(
                    role = role,
                    parts = listOf(GeminiPart(message.text))
                )
            )
            // update last role
            lastRole = role
        }
    }
    // start conversation from user always
    if(result.isNotEmpty() && result.first().role == "model"){
        result.removeAt(0)
    }

    return result
}