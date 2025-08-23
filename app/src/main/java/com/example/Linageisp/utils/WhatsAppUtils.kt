package com.example.Linageisp.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.Linageisp.data.Plan

/**
 * Utilidades para integración con WhatsApp
 * Maneja la contratación de planes vía WhatsApp con mensajes simplificados
 */
object WhatsAppUtils {
    
    private const val LINAGE_PHONE_NUMBER = "573004775856" // Sin el +
    
    /**
     * Función exacta según requerimientos para contratar plan por WhatsApp
     * @param context Contexto de la aplicación
     * @param planName Nombre del plan
     * @param planPrice Precio del plan
     */
    fun contractPlan(context: Context, planName: String, planPrice: String) {
        openWhatsApp(context, planName, planPrice)
    }
    
    /**
     * Función exacta según especificación del usuario
     * @param context Contexto de la aplicación  
     * @param planName Nombre del plan
     * @param planPrice Precio del plan
     */
    fun openWhatsApp(context: Context, planName: String, planPrice: String) {
        val phoneNumber = "573004775856"
        val message = "Hola, estoy interesado en contratar el plan $planName de $planPrice/mes"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://wa.me/$phoneNumber?text=${Uri.encode(message)}")
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback: mostrar toast con error
            Toast.makeText(
                context,
                "Error al abrir WhatsApp. Por favor instala WhatsApp o contacta al $phoneNumber",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    /**
     * Función simplificada para contratar plan usando objeto Plan
     * @param context Contexto de la aplicación  
     * @param plan Plan seleccionado para contratación
     */
    fun contractPlan(context: Context, plan: Plan) {
        contractPlan(context, plan.nombre, plan.precio)
    }
    
    /**
     * Abre WhatsApp con mensaje específico
     * @param context Contexto de la aplicación
     * @param message Mensaje a enviar
     */
    private fun openWhatsApp(context: Context, message: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://wa.me/$LINAGE_PHONE_NUMBER?text=${Uri.encode(message)}")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback: mostrar toast con error
            Toast.makeText(
                context,
                "Error al abrir WhatsApp. Por favor instala WhatsApp o contacta al $LINAGE_PHONE_NUMBER",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    /**
     * Mensaje de consulta general para WhatsApp
     * @param context Contexto de la aplicación
     */
    fun openGeneralConsultation(context: Context) {
        val message = """
🌐 ¡Hola! Me gustaría recibir información sobre los planes de internet de Linage ISP.

¿Podrían ayudarme con:
• Planes disponibles en mi zona
• Precios y promociones vigentes
• Proceso de contratación e instalación

¡Gracias! 😊
        """.trimIndent()
        
        openWhatsApp(context, message)
    }
    
    /**
     * Mensaje de soporte técnico para WhatsApp
     * @param context Contexto de la aplicación
     */
    fun openTechnicalSupport(context: Context) {
        val message = """
🔧 ¡Hola! Necesito asistencia técnica con mi servicio de internet Linage ISP.

Por favor ayúdenme con mi consulta técnica.

¡Gracias! 😊
        """.trimIndent()
        
        openWhatsApp(context, message)
    }
}