package com.example.Linageisp.data

/**
 * Modelo de datos para representar un plan de internet de Linage ISP
 * 
 * @param id Identificador único del plan
 * @param nombre Nombre del plan (ej: "5 MEGAS", "10 MEGAS")
 * @param velocidad Velocidad del plan como texto (ej: "5 Mbps descarga / 2.5 Mbps subida")
 * @param precio Precio del plan como texto (ej: "$25.000 COP/mes")
 * @param beneficios Beneficios del plan como texto separado por comas
 * @param type Tipo de conexión (Inalámbrico, Fibra, etc.)
 */
data class Plan(
    val id: String,
    val nombre: String,
    val velocidad: String,
    val precio: String,
    val beneficios: String,
    val type: String = ""
) {
    /**
     * Función auxiliar para obtener la velocidad de descarga extraída del texto
     * @return Velocidad de descarga en Mbps como entero, o 0 si no se puede extraer
     */
    fun getDownloadSpeed(): Int {
        return try {
            val regex = """(\d+)\s*Mbps\s*descarga""".toRegex(RegexOption.IGNORE_CASE)
            regex.find(velocidad)?.groupValues?.get(1)?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * Función auxiliar para obtener la velocidad de subida extraída del texto
     * @return Velocidad de subida en Mbps como entero, o 0 si no se puede extraer
     */
    fun getUploadSpeed(): Int {
        return try {
            val regex = """(\d+(?:\.\d+)?)\s*Mbps\s*subida""".toRegex(RegexOption.IGNORE_CASE)
            regex.find(velocidad)?.groupValues?.get(1)?.toDoubleOrNull()?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * Función auxiliar para obtener los beneficios como lista
     * @return Lista de beneficios separados y limpios
     */
    fun getBeneficiosList(): List<String> {
        return beneficios.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }
    
    /**
     * Función auxiliar para generar el mensaje de WhatsApp
     * @return Mensaje prellenado para WhatsApp con información del plan
     */
    fun getWhatsAppMessage(): String {
        return "Hola, estoy interesado en el plan $nombre. " +
                "Velocidad: $velocidad. " +
                "¿Podrían proporcionarme más información y confirmar el precio? Gracias."
    }
}

/**
 * Tipos de planes disponibles
 */
enum class PlanType(val displayName: String) {
    WIRELESS_AND_FIBER("Internet Inalámbrico y Fibra"),
    FIBER_ONLY("Internet por Fibra"),
    WIRELESS_ONLY("Internet Inalámbrico")
}