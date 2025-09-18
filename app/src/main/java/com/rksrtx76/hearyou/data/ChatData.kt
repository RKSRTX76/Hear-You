package com.rksrtx76.hearyou.data

import com.google.ai.client.generativeai.GenerativeModel
import com.rksrtx76.hearyou.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ChatData{
    val API_KEY = BuildConfig.API_KEY

    // We do not need retrofit as google did everything

    suspend fun getResponse(prompt : String) : Chat{
        val generativeModel = GenerativeModel(
            modelName = "gemini-2.5-pro",
            apiKey = API_KEY
        )

        try {
            val response = withContext(Dispatchers.IO){
                generativeModel.generateContent(prompt = prompt)
            }
            return Chat(
                prompt = response.text ?: "Error",
                isFromUser = false
            )
        }catch (e : Exception){  // in case prompt violets the rules
            return Chat(
                prompt = "Something went wrong, please try again later.",
                isFromUser = false
            )
        }
    }
}