package com.example.Linageisp.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Servicio de IA integrado con Firebase Genkit y Gemini 1.5 Flash
 * Proporciona capacidades de asistente virtual para Linage ISP
 */
class GenkitAIService {
    
    companion object {
        private const val API_KEY = "AIzaSyDTIw3sjgK089yq88UkcoYAbc0CLzf5imc"
        private const val MODEL_NAME = "gemini-1.5-flash"
    }
    
    private val generativeModel = GenerativeModel(
        modelName = MODEL_NAME,
        apiKey = API_KEY,
        generationConfig = generationConfig {
            temperature = 0.9f
            topK = 1
            topP = 1f
            maxOutputTokens = 2048
        }
    )
    
    /**
     * Asistente virtual para consultas sobre planes de internet
     * @param userQuestion Pregunta del usuario
     * @return Respuesta del asistente IA
     */
    suspend fun askAboutPlans(userQuestion: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                Eres un asistente virtual de Linage ISP, una empresa de internet en Colombia.
                
                Información de planes disponibles:
                
                📶 PLANES BÁSICOS (Fibra Óptica):
                - 200 Megas: $80.000 - 200 Mbps + Paramount+ 1 mes + TV 120 canales
                - 400 Megas: $85.000 - 400 Mbps + Paramount+ 1 mes + TV 120 canales
                - 600 Megas: $100.000 - 600 Mbps + Paramount+ 1 mes + TV 120 canales
                - 900 Megas: $115.000 - 900 Mbps + Paramount+ 1 mes + TV 120 canales
                
                ⚽ PLANES WIN+ PREMIUM:
                - Silver Win+: $113.000 - 400 Mbps + DIRECTV GO Flex + Win+ Fútbol + 2 pantallas + 3 meses Paramount+
                - Platinum Win+: $141.500 - 400 Mbps + DIRECTV GO Básico + Win+ Fútbol + 2 pantallas + 3 meses Paramount+
                - Gold Win+: $163.000 - 400 Mbps + DIRECTV GO FULL + Win+ Fútbol + 4 pantallas + 3 meses Paramount+
                
                📺 PLANES DIRECTV GO:
                - Silver DIRECTV: $90.000 - 400 Mbps + DIRECTV GO Flex + 1 pantalla + 3 meses Paramount+
                - Platinum DIRECTV: $118.500 - 400 Mbps + DIRECTV GO Básico + 2 pantallas + 3 meses Paramount+
                - Gold DIRECTV: $140.000 - 400 Mbps + DIRECTV GO FULL + 4 pantallas + 3 meses Paramount+
                
                🎬 PLANES NETFLIX:
                - Netflix 400: $85.000 - 400 Mbps + Netflix HD + 1 dispositivo
                - Netflix 600: $100.000 - 600 Mbps + Netflix HD + 1 dispositivo
                - Netflix 900: $115.000 - 900 Mbps + Netflix HD + 1 dispositivo
                
                📹 PLANES CON CÁMARAS:
                - Cámaras 400 MEGAS: $90.000 - 400 Mbps + 1 cámara de seguridad + Paramount+ 1 mes
                - Cámaras 600 MEGAS: $95.500 - 600 Mbps + 1 cámara de seguridad + Paramount+ 1 mes
                - Cámaras 900 MEGAS: $105.000 - 900 Mbps + 1 cámara de seguridad + Paramount+ 1 mes
                
                💼 ALIADOS COMERCIALES:
                - Lyon Sport: 25% descuento en ropa deportiva
                - MaCaDi Cosmetics: 30% descuento en productos de belleza
                - Netflix Premium: 3 meses gratis
                - Win Sports Max: Incluido gratis
                
                Pregunta del cliente: $userQuestion
                
                Instrucciones:
                - Responde de forma amigable, profesional y en español colombiano
                - Usa emojis relevantes para hacer la respuesta más atractiva
                - Si preguntan por precios, menciona que incluyen instalación gratis
                - Si no sabes algo específico, sugiere contactar WhatsApp: +57 302 447 8864
                - Sé específico con los beneficios de cada plan
                - Recomienda planes según las necesidades expresadas
            """.trimIndent()
            
            val response = generativeModel.generateContent(prompt)
            Result.success(response.text ?: "Lo siento, no pude procesar tu consulta. 😔")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Recomendador de planes basado en necesidades del usuario
     * @param familySize Número de personas en la familia
     * @param usage Tipo de uso: "básico", "streaming", "gaming", "trabajo"
     * @param budget Presupuesto: "económico", "medio", "premium"
     * @return Recomendación personalizada
     */
    suspend fun recommendPlan(
        familySize: Int,
        usage: String,
        budget: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                Como experto en telecomunicaciones de Linage ISP, recomienda el mejor plan para:
                
                👥 Tamaño de familia: $familySize personas
                🎯 Uso principal: $usage
                💰 Presupuesto: $budget
                
                PLANES DISPONIBLES CON PRECIOS:
                
                💡 ECONÓMICOS ($80k-$90k):
                - 200 Megas: $80.000 (básico familiar)
                - 400 Megas: $85.000 (streaming ligero)
                - Netflix 400: $85.000 (Netflix incluido)
                - Silver DIRECTV: $90.000 (TV premium básico)
                - Cámaras 400: $90.000 (seguridad + internet)
                
                💎 MEDIOS ($95k-$115k):
                - 600 Megas: $100.000 (streaming intensivo)
                - Netflix 600: $100.000 (Netflix + alta velocidad)
                - Cámaras 600: $95.500 (seguridad mejorada)
                - Netflix 900: $115.000 (máxima velocidad Netflix)
                - 900 Megas: $115.000 (velocidad extrema)
                - Silver Win+: $113.000 (deportes + streaming)
                - Platinum DIRECTV: $118.500 (TV premium + 2 pantallas)
                
                🔥 PREMIUM ($140k-$163k):
                - Gold DIRECTV: $140.000 (TV completo + 4 pantallas)
                - Platinum Win+: $141.500 (deportes premium)
                - Gold Win+: $163.000 (todo incluido premium)
                
                CRITERIOS DE RECOMENDACIÓN:
                - Básico: navegación, redes sociales, email
                - Streaming: Netflix, YouTube, plataformas de video
                - Gaming: juegos online, descargas, streaming
                - Trabajo: videollamadas, cloud, transferencias grandes
                
                - Económico: hasta $90.000
                - Medio: $95.000 - $120.000  
                - Premium: más de $140.000
                
                Proporciona UNA recomendación específica con:
                1. Plan exacto recomendado
                2. Precio
                3. Justificación clara
                4. Beneficios destacados
                5. Por qué es perfecto para su situación
                
                Usa emojis y sé persuasivo pero honesto.
            """.trimIndent()
            
            val response = generativeModel.generateContent(prompt)
            Result.success(response.text ?: "No pude generar una recomendación personalizada. 😔")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Soporte técnico inteligente
     * @param issue Descripción del problema técnico
     * @return Solución paso a paso
     */
    suspend fun technicalSupport(issue: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                Eres un técnico especializado de Linage ISP con 10+ años de experiencia.
                
                El cliente reporta el siguiente problema:
                🔧 Problema: $issue
                
                INFORMACIÓN TÉCNICA DE LINAGE ISP:
                - Tecnología: Fibra óptica GPON
                - Equipos: ONT + Router WiFi 6
                - Cobertura: Principalmente zona urbana Colombia
                - Velocidades: 200, 400, 600, 900 Mbps simétricos
                - Servicios adicionales: TV digital, cámaras IP, telefonía
                
                PROBLEMAS COMUNES Y SOLUCIONES:
                
                📶 CONECTIVIDAD:
                - Internet lento: verificar cables, reiniciar ONT/router, verificar interferencias
                - Sin internet: revisar luces ONT, cables, configuración
                - WiFi débil: reubicación router, cambio canal, repetidores
                
                📺 TV DIGITAL:
                - Sin señal: verificar conexiones HDMI/coaxial, reiniciar decodificador
                - Pixelación: verificar señal, cables, interferencias
                
                📹 CÁMARAS:
                - Sin imagen: verificar alimentación, configuración red, app
                - Grabación: verificar almacenamiento, configuración horarios
                
                ☎️ TELEFONÍA:
                - Sin línea: verificar conexión RJ11, configuración ATA
                
                Proporciona:
                1. ✅ Diagnóstico probable
                2. 🔧 Solución paso a paso (máximo 5 pasos)
                3. ⚠️ Si requiere visita técnica
                4. 💡 Consejos de prevención
                5. 📞 Cuándo contactar soporte: +57 302 447 8864
                
                Usa lenguaje técnico pero comprensible, con emojis para claridad.
                Si el problema es muy complejo, recomienda visita técnica inmediatamente.
            """.trimIndent()
            
            val response = generativeModel.generateContent(prompt)
            Result.success(response.text ?: "Por favor contacta a nuestro soporte técnico: +57 302 447 8864 📞")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Información general sobre Linage ISP
     * @param query Consulta general sobre la empresa
     * @return Información corporativa
     */
    suspend fun getCompanyInfo(query: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                Proporciona información sobre Linage ISP basada en la consulta: $query
                
                INFORMACIÓN CORPORATIVA:
                
                🏢 LINAGE ISP - Internet de Confianza
                - Empresa colombiana de telecomunicaciones
                - Especializada en fibra óptica residencial y empresarial
                - Cobertura en principales ciudades de Colombia
                - Tecnología GPON de última generación
                
                🎯 SERVICIOS:
                - Internet fibra óptica (200-900 Mbps)
                - Televisión digital HD (120+ canales)
                - DIRECTV GO integrado
                - Servicios streaming (Netflix, Paramount+)
                - Cámaras de seguridad IP
                - Telefonía fija
                - Win+ Fútbol deportes premium
                
                💎 VENTAJAS COMPETITIVAS:
                - Instalación 100% gratuita
                - Soporte técnico 24/7
                - Velocidades simétricas reales
                - Sin permanencia mínima
                - Aliados comerciales con descuentos
                - Activación inmediata
                
                📞 CONTACTO:
                - WhatsApp: +57 302 447 8864
                - Servicio al cliente 24/7
                - Visitas técnicas programadas
                - Soporte remoto especializado
                
                🏆 DIFERENCIADORES:
                - Red 100% fibra óptica
                - Equipos WiFi 6 incluidos
                - Streaming premium incluido
                - Deportes Win+ exclusivo
                - Cámaras seguridad integradas
                
                Responde específicamente a la consulta con información relevante y actualizada.
                Mantén un tono profesional pero cercano, usa emojis apropiados.
            """.trimIndent()
            
            val response = generativeModel.generateContent(prompt)
            Result.success(response.text ?: "Para más información, contacta: +57 302 447 8864 📞")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}