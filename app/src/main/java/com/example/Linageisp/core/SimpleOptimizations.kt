package com.example.Linageisp.core

import androidx.compose.runtime.Stable
import androidx.compose.runtime.Immutable

/**
 * Simplified optimization annotations and utilities
 * These provide basic performance benefits without complex implementations
 */

@Stable
annotation class PerformanceOptimized

@Immutable
data class CachedGradient(
    val colors: List<androidx.compose.ui.graphics.Color>
)

/**
 * Simple performance utilities that work
 */
object PerformanceUtils {
    /**
     * Simple cache for commonly used values
     */
    private val cache = mutableMapOf<String, Any>()
    
    fun <T> getCached(key: String, factory: () -> T): T {
        @Suppress("UNCHECKED_CAST")
        return cache.getOrPut(key) { factory() as Any } as T
    }
    
    fun clearCache() {
        cache.clear()
    }
}