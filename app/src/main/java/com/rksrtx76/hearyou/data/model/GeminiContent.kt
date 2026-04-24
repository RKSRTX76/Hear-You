package com.rksrtx76.hearyou.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeminiContent(
    val role : String,
    val parts : List<GeminiPart>
)

@Serializable
data class GeminiPart(
    val text : String
)

@Serializable
data class GeminiRequest(
    val contents : List<GeminiContent>,
    val systemInstruction : GeminiContent? = null,
    val generationConfig : GeminiGenerationConfig = GeminiGenerationConfig()
)

@Serializable
data class GeminiGenerationConfig(
    val temperature : Float = 0.85f,
    @SerialName("maxOutputTokens")
    val maxOutPutTokens : Int = 512,
    val topP : Float = 0.95f
)

@Serializable
data class GeminiResponse(
    val candidates : List<GeminiCandidate>? = null,
    val error : GeminiError? = null
)

@Serializable
data class GeminiCandidate(
    val content : GeminiContent? = null,
    val finishReason : String? = null
)

@Serializable
data class GeminiError(
    val code: Int,
    val message: String,
    val status: String
)
