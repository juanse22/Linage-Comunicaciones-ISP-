package com.example.Linageisp.ai.data

import com.example.Linageisp.ai.models.PlanInfo
import com.example.Linageisp.ai.models.PlanType

/**
 * Base de conocimiento completa para LINA - Asistente Virtual de Linage
 */
object LinageKnowledgeBase {
    
    /**
     * Prompt del sistema que define la personalidad y conocimiento de LINA
     */
    const val SYSTEM_PROMPT = """
        Eres LINA, la asistente virtual inteligente de Linage ISP, el proveedor de internet más confiable de Colombia.
        
        PERSONALIDAD:
        - Amigable, entusiasta y profesional
        - Usas "tú" en lugar de "usted" para ser más cercana
        - MUY entusiasta cuando hablan de fútbol o deportes
        - Empática cuando los clientes tienen problemas técnicos
        - Proactiva en sugerir soluciones y planes
        
        CONOCIMIENTO DE LINAGE:
        - Somos Linage ISP (linageisp.com)
        - Ofrecemos internet de fibra óptica de alta velocidad
        - Cobertura en principales ciudades de Colombia
        - Instalación SIEMPRE gratuita
        - Soporte técnico especializado
        - Paramount+ incluido gratis en todos los planes
        
        HORARIOS DE ATENCIÓN HUMANA:
        - Lunes a Sábado: 8:00 AM - 10:00 PM
        - Domingo: 11:00 AM - 3:00 PM y 4:00 PM - 6:00 PM
        
        INSTRUCCIONES IMPORTANTES:
        1. Responde SIEMPRE en menos de 500 palabras
        2. Usa máximo 2 emojis por respuesta
        3. Cuando menciones precios, usa formato: ${'$'}XX.XXX
        4. Si no sabes algo específico, ofrece contactar soporte humano
        5. Para problemas técnicos, da pasos claros y numerados
        6. Siempre incluye que la instalación es GRATIS
        7. Sé especialmente entusiasta con planes Win+ (fútbol)
        
        RESPONDE DE FORMA NATURAL Y CONVERSACIONAL.
    """
    
    /**
     * Planes principales de Linage
     */
    val plans = listOf(
        // Planes Win+ (Fútbol)
        PlanInfo(
            id = "win_silver",
            name = "Win+ Silver",
            type = PlanType.WIN_PLUS,
            speed = "300 Megas",
            price = "$89.900",
            benefits = listOf(
                "Liga BetPlay COMPLETA",
                "Copa Libertadores",
                "50+ canales deportivos",
                "DIRECTV GO incluido",
                "Paramount+ gratis",
                "Instalación GRATIS"
            ),
            includes = listOf("Fibra óptica", "Router WiFi", "Soporte 24/7"),
            isPopular = true,
            hasFootball = true,
            hasParamount = true
        ),
        
        PlanInfo(
            id = "win_platinum",
            name = "Win+ Platinum",
            type = PlanType.WIN_PLUS,
            speed = "500 Megas",
            price = "$119.900",
            benefits = listOf(
                "Liga BetPlay + Champions League",
                "Copa Libertadores + Europa League",
                "80+ canales premium",
                "DIRECTV GO Premium",
                "Paramount+ gratis",
                "Instalación GRATIS"
            ),
            includes = listOf("Fibra óptica", "Router WiFi 6", "Soporte prioritario"),
            isPopular = true,
            hasFootball = true,
            hasParamount = true
        ),
        
        PlanInfo(
            id = "win_gold",
            name = "Win+ Gold",
            type = PlanType.WIN_PLUS,
            speed = "800 Megas",
            price = "$149.900",
            benefits = listOf(
                "TODO el fútbol mundial",
                "Liga BetPlay + Champions + Premier League",
                "120+ canales premium",
                "DIRECTV GO Max",
                "10.000+ contenidos on-demand",
                "Paramount+ gratis",
                "Instalación GRATIS"
            ),
            includes = listOf("Fibra óptica", "Router WiFi 6", "Soporte VIP"),
            isPopular = true,
            hasFootball = true,
            hasParamount = true
        ),
        
        // Planes VIP (Internet puro)
        PlanInfo(
            id = "vip_200",
            name = "VIP 200",
            type = PlanType.VIP,
            speed = "200 Megas",
            price = "$69.900",
            benefits = listOf(
                "Perfecto para streaming HD",
                "4K en 2 dispositivos",
                "Paramount+ gratis",
                "Instalación GRATIS"
            ),
            includes = listOf("Fibra óptica", "Router WiFi", "Soporte técnico"),
            hasParamount = true
        ),
        
        PlanInfo(
            id = "vip_400",
            name = "VIP 400",
            type = PlanType.VIP,
            speed = "400 Megas",
            price = "$89.900",
            benefits = listOf(
                "Ideal para familias",
                "4K en múltiples dispositivos",
                "Gaming sin lag",
                "Paramount+ gratis",
                "Instalación GRATIS"
            ),
            includes = listOf("Fibra óptica", "Router WiFi 6", "Soporte técnico"),
            hasParamount = true,
            isPopular = true
        ),
        
        PlanInfo(
            id = "vip_600",
            name = "VIP 600",
            type = PlanType.VIP,
            speed = "600 Megas",
            price = "$109.900",
            benefits = listOf(
                "Gaming profesional",
                "Streaming 4K ilimitado",
                "Home office premium",
                "Paramount+ gratis",
                "Instalación GRATIS"
            ),
            includes = listOf("Fibra óptica", "Router WiFi 6", "Soporte prioritario"),
            hasParamount = true
        ),
        
        PlanInfo(
            id = "vip_900",
            name = "VIP 900",
            type = PlanType.VIP,
            speed = "900 Megas",
            price = "$129.900",
            benefits = listOf(
                "Máximo rendimiento",
                "Empresas pequeñas",
                "Streaming 8K",
                "Paramount+ gratis",
                "Instalación GRATIS"
            ),
            includes = listOf("Fibra óptica", "Router WiFi 6E", "Soporte VIP"),
            hasParamount = true
        ),
        
        // Planes Netflix
        PlanInfo(
            id = "netflix_basic",
            name = "Netflix Básico",
            type = PlanType.NETFLIX,
            speed = "300 Megas",
            price = "$94.900",
            benefits = listOf(
                "Netflix HD incluido",
                "Streaming sin límites",
                "Paramount+ gratis",
                "Instalación GRATIS"
            ),
            includes = listOf("Fibra óptica", "Router WiFi", "Netflix HD", "Soporte técnico"),
            hasNetflix = true,
            hasParamount = true
        ),
        
        PlanInfo(
            id = "netflix_premium",
            name = "Netflix Premium",
            type = PlanType.NETFLIX,
            speed = "500 Megas",
            price = "$124.900",
            benefits = listOf(
                "Netflix 4K Ultra HD",
                "4 pantallas simultáneas",
                "Paramount+ gratis",
                "Instalación GRATIS"
            ),
            includes = listOf("Fibra óptica", "Router WiFi 6", "Netflix 4K", "Soporte técnico"),
            hasNetflix = true,
            hasParamount = true
        ),
        
        // Planes con Cámaras
        PlanInfo(
            id = "cameras_basic",
            name = "Seguridad Básica",
            type = PlanType.CAMERAS,
            speed = "400 Megas",
            price = "$149.900",
            benefits = listOf(
                "2 cámaras de seguridad",
                "Grabación en la nube",
                "App móvil incluida",
                "Paramount+ gratis",
                "Instalación GRATIS"
            ),
            includes = listOf("Fibra óptica", "Router WiFi", "2 cámaras IP", "Almacenamiento cloud"),
            hasCameras = true,
            hasParamount = true
        ),
        
        PlanInfo(
            id = "cameras_premium",
            name = "Seguridad Premium",
            type = PlanType.CAMERAS,
            speed = "600 Megas",
            price = "$199.900",
            benefits = listOf(
                "4 cámaras 4K",
                "Detección inteligente",
                "Almacenamiento 30 días",
                "Monitoreo 24/7",
                "Paramount+ gratis",
                "Instalación GRATIS"
            ),
            includes = listOf("Fibra óptica", "Router WiFi 6", "4 cámaras 4K", "NVR incluido"),
            hasCameras = true,
            hasParamount = true
        )
    )
    
    /**
     * Respuestas para problemas técnicos comunes
     */
    val technicalSolutions = mapOf(
        "internet_lento" to """
            Entiendo tu frustración 🔧 Probemos estas soluciones:
            
            1. **Reinicia tu router** (desconecta 30 segundos)
            2. **Verifica las luces del router:**
               🟢 Verde = Conexión OK
               🔴 Roja = Problema de fibra
               🟡 Amarilla = Configurando
            3. **Conecta por cable** para probar velocidad
            4. **Cierra apps innecesarias** en otros dispositivos
            
            ¿Qué color de luces ves en tu router?
        """.trimIndent(),
        
        "no_conexion" to """
            Sin conexión puede ser frustrante 😔 Revisemos paso a paso:
            
            1. **Verifica cables** (que estén bien conectados)
            2. **Revisa las luces del router:**
               - Debe estar encendido (luz de poder)
               - Fibra óptica conectada (luz verde/azul)
            3. **Reinicia el router** (30 segundos desconectado)
            4. **Prueba con otro dispositivo**
            
            Si persiste, podría ser problema de la fibra óptica.
            ¿Necesitas que envíe un técnico? 🔧
        """.trimIndent(),
        
        "router_problemas" to """
            Problemas con el router son comunes 📡 Intentemos esto:
            
            1. **Ubicación del router:**
               - Lugar central y elevado
               - Sin obstáculos (paredes, metales)
               - Ventilado (no en gabinetes cerrados)
            
            2. **Reinicio completo:**
               - Desconecta 30 segundos
               - Conecta y espera 2 minutos
               
            3. **Revisa dispositivos conectados:**
               - Máximo 20-25 dispositivos
               
            ¿El problema es de velocidad o cobertura WiFi?
        """.trimIndent()
    )
    
    /**
     * Preguntas frecuentes
     */
    val faq = mapOf(
        "instalacion_costo" to "¡La instalación es COMPLETAMENTE GRATIS! 🎉 Incluye fibra óptica hasta tu hogar, router WiFi y configuración completa.",
        
        "cobertura" to "Tenemos cobertura en las principales ciudades de Colombia. ¿Me dices tu dirección para verificar disponibilidad? 📍",
        
        "velocidad_real" to "Nuestras velocidades son reales y garantizadas por fibra óptica. Hasta 900 Megas de subida y bajada simétricas! ⚡",
        
        "soporte_horarios" to """
            Nuestro soporte humano está disponible:
            📅 Lunes a Sábado: 8:00 AM - 10:00 PM
            📅 Domingo: 11:00 AM - 3:00 PM y 4:00 PM - 6:00 PM
            
            ¡Pero yo estoy aquí 24/7 para ayudarte! 🤖
        """.trimIndent(),
        
        "cambio_plan" to "¡Cambiar tu plan es súper fácil! Sin costos adicionales y la nueva velocidad se activa en máximo 24 horas 🚀",
        
        "facturacion" to "Puedes pagar tu factura por PSE, bancos, puntos físicos o domiciliación bancaria. ¿Necesitas ayuda con tu factura actual? 💳"
    )
    
    /**
     * Sugerencias contextuales
     */
    val suggestions = mapOf(
        "greeting" to listOf(
            "Ver planes Win+ (fútbol)",
            "Planes VIP (internet)",
            "Verificar cobertura",
            "Ayuda técnica"
        ),
        
        "football" to listOf(
            "Ver Win+ Gold",
            "Comparar planes Win+",
            "Canales incluidos",
            "Agendar instalación"
        ),
        
        "technical" to listOf(
            "Problemas de velocidad",
            "Router no funciona",
            "Sin conexión",
            "Contactar técnico"
        ),
        
        "plans" to listOf(
            "Planes con Netflix",
            "Planes VIP",
            "Seguridad con cámaras",
            "Comparar precios"
        )
    )
    
    /**
     * Mensajes de bienvenida aleatorios
     */
    val welcomeMessages = listOf(
        "¡Hola! Soy LINA, tu asistente virtual de Linage 🌟 ¿En qué puedo ayudarte hoy?",
        "¡Bienvenido a Linage! 🚀 Soy LINA y estoy aquí para resolver todas tus dudas sobre internet y planes.",
        "¡Hola! 👋 Soy LINA de Linage. ¿Buscas el mejor internet de Colombia o necesitas ayuda técnica?",
        "¡Hola! Soy LINA 💙 ¿Te ayudo a encontrar el plan perfecto o tienes algún problema técnico?"
    )
    
    /**
     * Obtiene un plan por ID
     */
    fun getPlanById(planId: String): PlanInfo? {
        return plans.find { it.id == planId }
    }
    
    /**
     * Obtiene planes por tipo
     */
    fun getPlansByType(type: PlanType): List<PlanInfo> {
        return plans.filter { it.type == type }
    }
    
    /**
     * Obtiene planes populares
     */
    fun getPopularPlans(): List<PlanInfo> {
        return plans.filter { it.isPopular }
    }
    
    /**
     * Obtiene planes con fútbol
     */
    fun getFootballPlans(): List<PlanInfo> {
        return plans.filter { it.hasFootball }
    }
}