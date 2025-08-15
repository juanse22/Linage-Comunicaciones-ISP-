package com.example.Linageisp.ai.responses

import com.example.Linageisp.ai.data.LinageKnowledgeBase
import com.example.Linageisp.ai.models.*

/**
 * Respuestas especializadas y contextuales para diferentes tipos de consultas
 */
object SpecializedResponses {
    
    /**
     * Respuestas entusiastas para consultas sobre fútbol y planes Win+
     */
    fun getFootballResponse(query: String): AIResponse {
        val content = when {
            query.contains("liga", true) || query.contains("betplay", true) -> {
                """
                ⚽ ¡EXCELENTE ELECCIÓN! Con nuestros planes Win+ de Linage ves toda la Liga BetPlay:
                
                🏆 **Win+ Silver** ($89.900): Liga BetPlay completa
                🏆 **Win+ Platinum** ($119.900): Liga + Champions League  
                🏆 **Win+ Gold** ($149.900): TODO el fútbol mundial
                
                ¡Instalación GRATIS y Paramount+ incluido! 🎉
                ¿Cuál te llama más la atención?
                """.trimIndent()
            }
            
            query.contains("champions", true) -> {
                """
                🏆 ¡La Champions League te espera! Con Win+ Platinum o Gold tienes:
                
                ✨ **Todos los partidos en vivo**
                ✨ **Repeticiones y highlights**
                ✨ **DIRECTV GO incluido**
                ✨ **Instalación GRATIS**
                
                ¿Te imaginas ver el Real Madrid vs Manchester City en 4K desde tu casa? 🚀
                """.trimIndent()
            }
            
            query.contains("partido", true) || query.contains("ver", true) -> {
                """
                ⚽ ¡No te pierdas ni UN solo partido! Con Win+ de Linage tienes:
                
                📺 **80+ canales deportivos**
                📺 **Liga BetPlay completa**
                📺 **Champions, Libertadores y más**
                📺 **Calidad 4K garantizada**
                
                ¡Y la instalación es GRATIS! ¿Agendamos para mañana? 😉
                """.trimIndent()
            }
            
            else -> {
                """
                ⚽ ¡Los planes Win+ son PERFECTOS para los amantes del fútbol!
                
                🥇 **Win+ Gold**: TODO el fútbol mundial
                🥈 **Win+ Platinum**: Liga + Champions + Europa League
                🥉 **Win+ Silver**: Liga BetPlay completa
                
                ¡Instalación GRATIS y velocidades hasta 800 Megas! 🚀
                """.trimIndent()
            }
        }
        
        val quickActions = listOf(
            QuickAction("view_win_gold", "Ver Win+ Gold", ActionType.VIEW_PLAN, "⚽"),
            QuickAction("view_win_platinum", "Ver Win+ Platinum", ActionType.VIEW_PLAN, "🏆"),
            QuickAction("schedule_install", "Agendar instalación", ActionType.SCHEDULE_CALL, "📅"),
            QuickAction("view_channels", "Ver canales", ActionType.VIEW_CHANNELS, "📺")
        )
        
        return AIResponse(
            content = content,
            intent = Intent.PLANS_FOOTBALL,
            confidence = 0.95f,
            quickActions = quickActions,
            suggestedPlans = LinageKnowledgeBase.getFootballPlans()
        )
    }
    
    /**
     * Respuestas estructuradas para problemas técnicos
     */
    fun getTechnicalSupportResponse(issue: String): AIResponse {
        val content = when {
            issue.contains("lento", true) || issue.contains("velocidad", true) -> {
                """
                Entiendo tu frustración con la velocidad 🔧 Probemos estos pasos:
                
                **1. Diagnóstico rápido:**
                   • Reinicia el router (30 segundos desconectado)
                   • Verifica las luces: 🟢 Verde = OK, 🔴 Roja = Problema
                
                **2. Prueba de velocidad:**
                   • Conecta por cable directo al router
                   • Cierra otras aplicaciones y dispositivos
                
                **3. Optimización WiFi:**
                   • Router en lugar central y elevado
                   • Sin obstáculos metálicos cerca
                
                ¿Qué color de luces ves en tu router?
                """.trimIndent()
            }
            
            issue.contains("no funciona", true) || issue.contains("sin internet", true) -> {
                """
                Sin conexión puede ser muy frustrante 😔 Revisemos paso a paso:
                
                **1. Verificación básica:**
                   • Router encendido (luz de poder)
                   • Cables bien conectados
                   • Cable de fibra no doblado/dañado
                
                **2. Reinicio completo:**
                   • Desconecta router 30 segundos
                   • Conecta y espera 2 minutos completos
                
                **3. Luces del router:**
                   🟢 Verde/Azul = Fibra OK
                   🔴 Roja = Problema de línea
                
                Si las luces están rojas, necesitas un técnico. ¿Las envío?
                """.trimIndent()
            }
            
            issue.contains("wifi", true) || issue.contains("señal", true) -> {
                """
                Problemas de WiFi tienen solución fácil 📡 Intentemos:
                
                **1. Ubicación del router:**
                   • Lugar central de la casa
                   • Altura media (mesa, no suelo)
                   • Lejos de microondas/metales
                
                **2. Optimización:**
                   • Reinicia router (30 segundos)
                   • Cambia canal WiFi (automático)
                   • Máximo 25 dispositivos conectados
                
                **3. Cobertura:**
                   • ¿La señal es débil en toda la casa?
                   • Podríamos necesitar repetidor WiFi
                
                ¿En qué parte de la casa falla más la señal?
                """.trimIndent()
            }
            
            else -> {
                """
                Estoy aquí para ayudarte con cualquier problema técnico 🛠️
                
                **Problemas más comunes:**
                • Internet lento → Reinicio y optimización
                • Sin conexión → Verificación de cables y luces
                • WiFi débil → Reubicación del router
                • Canales que no cargan → Reinicio del decodificador
                
                ¿Podrías describir exactamente qué está pasando?
                Mientras más detalles, mejor te puedo ayudar 😊
                """.trimIndent()
            }
        }
        
        val quickActions = listOf(
            QuickAction("restart_router", "Reiniciar router", ActionType.RESTART_ROUTER, "🔄"),
            QuickAction("speed_test", "Prueba de velocidad", ActionType.SPEED_TEST, "⚡"),
            QuickAction("contact_tech", "Contactar técnico", ActionType.CONTACT_SUPPORT, "🔧"),
            QuickAction("tech_help", "Más ayuda técnica", ActionType.TECHNICAL_HELP, "❓")
        )
        
        return AIResponse(
            content = content,
            intent = Intent.TECHNICAL_SUPPORT,
            confidence = 0.90f,
            quickActions = quickActions
        )
    }
    
    /**
     * Respuestas comparativas para consultas de planes generales
     */
    fun getPlansComparisonResponse(planType: String? = null): AIResponse {
        val content = when (planType?.lowercase()) {
            "vip" -> {
                """
                ¡Los planes VIP son perfectos para internet puro! 🚀
                
                💎 **VIP 200** ($69.900): Streaming HD perfecto
                💎 **VIP 400** ($89.900): Familias y gaming
                💎 **VIP 600** ($109.900): Gaming profesional  
                💎 **VIP 900** ($129.900): Máximo rendimiento
                
                **Todos incluyen:**
                ✅ Fibra óptica real
                ✅ Paramount+ gratis
                ✅ Instalación GRATIS
                
                ¿Para cuántas personas es el internet?
                """.trimIndent()
            }
            
            "netflix" -> {
                """
                ¡Internet + Netflix = Combo perfecto! 🎬
                
                🎯 **Netflix Básico** ($94.900):
                   • 300 Megas + Netflix HD
                   • Paramount+ gratis
                
                🎯 **Netflix Premium** ($124.900):
                   • 500 Megas + Netflix 4K Ultra HD
                   • 4 pantallas simultáneas
                   • Paramount+ gratis
                
                ¡Ya no pagas Netflix por separado! 🎉
                ¿Cuántas personas van a usar Netflix?
                """.trimIndent()
            }
            
            else -> {
                """
                ¡Tenemos el plan PERFECTO para ti! 🎯
                
                **Si amas el fútbol:**
                ⚽ Win+ Plans - Desde $89.900
                
                **Si quieres internet puro:**
                💎 VIP Plans - Desde $69.900
                
                **Si usas Netflix mucho:**
                🎬 Netflix Plans - Desde $94.900
                
                **Si necesitas seguridad:**
                🛡️ Planes con cámaras - Desde $149.900
                
                **Todos con instalación GRATIS** 🎉
                ¿Cuál te interesa más?
                """.trimIndent()
            }
        }
        
        val quickActions = listOf(
            QuickAction("compare_plans", "Comparar todos", ActionType.VIEW_PLAN, "📊"),
            QuickAction("check_coverage", "Verificar cobertura", ActionType.CHECK_COVERAGE, "📍"),
            QuickAction("schedule_call", "Agendar llamada", ActionType.SCHEDULE_CALL, "📞"),
            QuickAction("view_popular", "Ver más populares", ActionType.VIEW_PLAN, "🔥")
        )
        
        return AIResponse(
            content = content,
            intent = Intent.PLANS_GENERAL,
            confidence = 0.85f,
            quickActions = quickActions,
            suggestedPlans = LinageKnowledgeBase.getPopularPlans()
        )
    }
    
    /**
     * Respuestas para consultas de cobertura
     */
    fun getCoverageResponse(): AIResponse {
        val content = """
            ¡Verificar cobertura es súper fácil! 📍
            
            **Tenemos cobertura en:**
            🏙️ Bogotá y área metropolitana
            🏙️ Medellín y Valle de Aburrá  
            🏙️ Cali y municipios cercanos
            🏙️ Barranquilla y área metropolitana
            🏙️ Cartagena y zona urbana
            
            **Para verificar tu dirección exacta:**
            📱 Compárteme tu dirección completa
            📱 O te conectamos con un asesor
            
            ¡Si hay cobertura, instalamos en 24-48 horas! ⚡
            """.trimIndent()
        
        val quickActions = listOf(
            QuickAction("contact_coverage", "Verificar mi dirección", ActionType.CONTACT_SUPPORT, "📍"),
            QuickAction("view_plans", "Ver planes disponibles", ActionType.VIEW_PLAN, "📋"),
            QuickAction("schedule_visit", "Agendar visita técnica", ActionType.SCHEDULE_CALL, "🏠")
        )
        
        return AIResponse(
            content = content,
            intent = Intent.COVERAGE,
            confidence = 0.80f,
            quickActions = quickActions
        )
    }
    
    /**
     * Respuestas para problemas de facturación
     */
    fun getBillingResponse(query: String): AIResponse {
        val content = when {
            query.contains("pagar", true) || query.contains("pago", true) -> {
                """
                ¡Pagar tu factura es súper fácil! 💳
                
                **Métodos de pago disponibles:**
                🏦 PSE (Pago Seguro en Línea)
                🏦 Bancos (todas las entidades)
                🏦 Corresponsales bancarios
                🏦 Domiciliación automática
                
                **Pago en línea:**
                💻 Portal web linageisp.com
                📱 App móvil Linage
                
                ¿Necesitas ayuda con un pago específico? 😊
                """.trimIndent()
            }
            
            query.contains("factura", true) || query.contains("cuenta", true) -> {
                """
                Para consultar tu factura puedo ayudarte 📄
                
                **Información disponible:**
                📋 Estado de cuenta actual
                📋 Histórico de pagos
                📋 Fecha de vencimiento
                📋 Valor a pagar
                
                **¿Qué necesitas saber?**
                • ¿Cuánto debo?
                • ¿Cuándo vence mi factura?
                • ¿Cómo descargar mi factura?
                
                Necesito algunos datos para ayudarte mejor 🔐
                """.trimIndent()
            }
            
            else -> {
                """
                Estoy aquí para ayudarte con temas de facturación 💼
                
                **Puedo ayudarte con:**
                💳 Información de pagos
                📄 Consulta de facturas
                📅 Fechas de vencimiento
                🔄 Problemas de facturación
                
                ¿Qué información específica necesitas?
                """.trimIndent()
            }
        }
        
        val quickActions = listOf(
            QuickAction("billing_info", "Consultar factura", ActionType.BILLING_INFO, "📄"),
            QuickAction("payment_help", "Ayuda con pagos", ActionType.CONTACT_SUPPORT, "💳"),
            QuickAction("contact_billing", "Contactar facturación", ActionType.CONTACT_SUPPORT, "📞")
        )
        
        return AIResponse(
            content = content,
            intent = Intent.BILLING,
            confidence = 0.85f,
            quickActions = quickActions
        )
    }
    
    /**
     * Respuesta de bienvenida personalizada
     */
    fun getWelcomeResponse(): AIResponse {
        val welcomeMessage = LinageKnowledgeBase.welcomeMessages.random()
        
        val quickActions = listOf(
            QuickAction("football_plans", "Ver planes Win+ ⚽", ActionType.VIEW_PLAN, "⚽"),
            QuickAction("vip_plans", "Ver planes VIP", ActionType.VIEW_PLAN, "💎"),
            QuickAction("check_coverage", "Verificar cobertura", ActionType.CHECK_COVERAGE, "📍"),
            QuickAction("technical_help", "Ayuda técnica", ActionType.TECHNICAL_HELP, "🔧")
        )
        
        return AIResponse(
            content = welcomeMessage,
            intent = Intent.GREETING,
            confidence = 1.0f,
            quickActions = quickActions,
            suggestedPlans = LinageKnowledgeBase.getPopularPlans().take(3)
        )
    }
    
    /**
     * Respuesta de despedida
     */
    fun getGoodbyeResponse(): AIResponse {
        val content = """
            ¡Ha sido un placer ayudarte! 😊
            
            **Recuerda que siempre estoy aquí para:**
            ⚽ Consultas sobre planes Win+
            💎 Información de planes VIP  
            🔧 Soporte técnico
            📍 Verificación de cobertura
            
            **¡Que tengas un excelente día!** 🌟
            
            Y no olvides: ¡Con Linage tienes la mejor fibra óptica! 🚀
            """.trimIndent()
        
        val quickActions = listOf(
            QuickAction("view_plans", "Ver planes", ActionType.VIEW_PLAN, "📋"),
            QuickAction("contact_support", "Contactar soporte", ActionType.CONTACT_SUPPORT, "📞")
        )
        
        return AIResponse(
            content = content,
            intent = Intent.GOODBYE,
            confidence = 1.0f,
            quickActions = quickActions
        )
    }
    
    /**
     * Respuesta de ayuda general
     */
    fun getHelpResponse(): AIResponse {
        val content = """
            ¡Estoy aquí para ayudarte con todo lo relacionado a Linage! 🤖
            
            **¿En qué puedo ayudarte?**
            ⚽ **Planes de fútbol** - Win+ con Liga BetPlay y Champions
            💎 **Planes VIP** - Internet puro de alta velocidad
            🎬 **Planes Netflix** - Internet + Netflix incluido
            🔧 **Soporte técnico** - Solución a problemas de internet
            📍 **Cobertura** - Verificar disponibilidad en tu zona
            💳 **Facturación** - Consultas de pagos y facturas
            
            **¡Solo dime qué necesitas y te ayudo al instante!** 😊
            """.trimIndent()
        
        val quickActions = listOf(
            QuickAction("football_plans", "Planes de fútbol", ActionType.VIEW_PLAN, "⚽"),
            QuickAction("vip_plans", "Planes VIP", ActionType.VIEW_PLAN, "💎"),
            QuickAction("technical_support", "Soporte técnico", ActionType.TECHNICAL_HELP, "🔧"),
            QuickAction("check_coverage", "Verificar cobertura", ActionType.CHECK_COVERAGE, "📍")
        )
        
        return AIResponse(
            content = content,
            intent = Intent.HELP,
            confidence = 1.0f,
            quickActions = quickActions,
            suggestedPlans = LinageKnowledgeBase.getPopularPlans().take(3)
        )
    }

    /**
     * Respuesta cuando no se puede resolver la consulta
     */
    fun getEscalationResponse(): AIResponse {
        val content = """
            Entiendo que necesitas ayuda más específica 🤔
            
            **Te conecto con nuestro equipo humano:**
            📞 Líneas de atención especializadas
            📱 WhatsApp empresarial
            💬 Chat con asesores expertos
            
            **Horarios de atención:**
            📅 Lunes a Sábado: 8:00 AM - 10:00 PM
            📅 Domingo: 11:00 AM - 3:00 PM y 4:00 PM - 6:00 PM
            
            ¡Ellos resolverán tu consulta al 100%! 😊
            """.trimIndent()
        
        val quickActions = listOf(
            QuickAction("contact_human", "Contactar asesor", ActionType.CONTACT_SUPPORT, "👨‍💼"),
            QuickAction("schedule_call", "Agendar llamada", ActionType.SCHEDULE_CALL, "📞"),
            QuickAction("try_again", "Intentar de nuevo", ActionType.HELP, "🔄")
        )
        
        return AIResponse(
            content = content,
            intent = Intent.ESCALATE_HUMAN,
            confidence = 0.70f,
            quickActions = quickActions,
            shouldEscalate = true
        )
    }
}