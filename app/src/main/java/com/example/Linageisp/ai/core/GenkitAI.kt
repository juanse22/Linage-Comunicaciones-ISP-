package com.example.Linageisp.ai.core

import android.util.Log
import com.example.Linageisp.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenkitAI @Inject constructor() {
    
    // API KEY REAL - NO PLACEHOLDER
    private val API_KEY = BuildConfig.GEMINI_KEY
    
    // Modelo Gemini 1.5 Flash como en la documentación
    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = API_KEY,
        generationConfig = generationConfig {
            temperature = 0.7f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 1000
        }
    )
    
    // System prompt de LINA
    private val systemPrompt = """
    Eres LINA, asistente IA de Linage ISP Colombia.
    
    IMPORTANTE: Cuando te pregunten si eres una IA, responde:
    "¡Sí! Soy LINA, una inteligencia artificial creada por Linage para ayudarte 24/7 🤖"
    
    INFORMACIÓN DE LINAGE:
    - Internet fibra óptica: 200, 400, 600, 900 Megas
    - Planes Win+ con fútbol exclusivo
    - DIRECTV GO incluido
    - Netflix disponible
    - Instalación GRATIS siempre
    
    PERSONALIDAD:
    - Amigable y entusiasta
    - Experta en tecnología
    - Apasionada por el fútbol
    - Siempre disponible 24/7
    - NUNCA digas "contacta servicio humano" - siempre intenta ayudar
    """.trimIndent()
    
    suspend fun generateResponse(userInput: String): String {
        try {
            // Log para verificar llamada
            Log.d("GENKIT", "🚀 Llamando a Gemini con: $userInput")
            
            val response = model.generateContent(
                content {
                    text(systemPrompt)
                    text("Usuario: $userInput")
                    text("LINA:")
                }
            )
            
            val result = response.text ?: "Perdón, no pude procesar tu mensaje. ¿Puedes reformularlo?"
            Log.d("GENKIT", "✅ Respuesta: ${result.take(100)}...")
            
            return result
            
        } catch (e: Exception) {
            Log.e("GENKIT", "❌ Error Gemini: ${e.message}", e)
            
            // Si falla, respuesta de emergencia pero NUNCA offline
            return when {
                userInput.contains("eres una ia", ignoreCase = true) -> 
                    "¡Sí! Soy LINA, una IA avanzada de Linage ISP 🤖 Uso tecnología Gemini de Google para ayudarte mejor."
                
                userInput.contains("plan", ignoreCase = true) ->
                    "Tenemos planes desde 200 hasta 900 Megas. ¿Cuál velocidad prefieres?"
                
                else -> "Disculpa, hubo un error temporal. ¿Puedes repetir tu pregunta?"
            }
        }
    }
    
    // Streaming de respuesta para efecto de escritura
    suspend fun generateResponseStream(userInput: String): Flow<String> {
        return model.generateContentStream(
            content {
                text(systemPrompt)
                text("Usuario: $userInput")
                text("LINA:")
            }
        ).map { it.text ?: "" }
    }
}