package com.example.Linageisp.ui.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

/**
 * Singleton object for shared animations to optimize performance
 * Reduces animation overhead by reusing transition states
 * OPTIMIZADO: Evita crear múltiples transiciones simultáneas
 */
object SharedAnimations {
    
    // Cache compartido para transiciones infinitas
    private var cachedInfiniteTransition: InfiniteTransition? = null
    
    /**
     * Shared infinite transition for all infinite animations in the app
     * This prevents creating multiple infinite transitions which can impact performance
     * OPTIMIZADO: Usa cache para reutilizar la misma transición
     */
    @Composable
    fun rememberSharedInfiniteTransition(): InfiniteTransition {
        return cachedInfiniteTransition ?: rememberInfiniteTransition(label = "shared_infinite_transition").also { 
            cachedInfiniteTransition = it 
        }
    }
    
    /**
     * Optimized float animation with configurable easing and duration
     */
    @Composable
    fun rememberOptimizedFloat(
        initialValue: Float,
        targetValue: Float,
        durationMillis: Int = 800,
        delayMillis: Int = 0,
        easing: Easing = EaseInOutCubic
    ): State<Float> {
        return animateFloatAsState(
            targetValue = targetValue,
            animationSpec = tween(
                durationMillis = durationMillis,
                delayMillis = delayMillis,
                easing = easing
            ),
            label = "optimized_float_animation"
        )
    }
    
    /**
     * Optimized color animation for theme transitions
     */
    @Composable
    fun rememberOptimizedColor(
        targetColor: Color,
        durationMillis: Int = 600,
        easing: Easing = EaseInOutCubic
    ): State<Color> {
        return androidx.compose.animation.animateColorAsState(
            targetValue = targetColor,
            animationSpec = tween(
                durationMillis = durationMillis,
                easing = easing
            ),
            label = "optimized_color_animation"
        )
    }
    
    /**
     * Shared spring animation for consistent bounce effects
     */
    @Composable
    fun rememberSharedSpring(
        targetValue: Float,
        dampingRatio: Float = Spring.DampingRatioMediumBouncy,
        stiffness: Float = Spring.StiffnessHigh
    ): State<Float> {
        return animateFloatAsState(
            targetValue = targetValue,
            animationSpec = spring(
                dampingRatio = dampingRatio,
                stiffness = stiffness
            ),
            label = "shared_spring_animation"
        )
    }
    
    /**
     * Optimized pulse animation for loading states
     */
    @Composable
    fun rememberPulseAnimation(): State<Float> {
        val infiniteTransition = rememberSharedInfiniteTransition()
        
        return infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = EaseInOutCubic),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_animation"
        )
    }
    
    /**
     * Optimized rotation animation for loading spinners
     */
    @Composable
    fun rememberRotationAnimation(): State<Float> {
        val infiniteTransition = rememberSharedInfiniteTransition()
        
        return infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation_animation"
        )
    }
    
    /**
     * Staggered entrance animations for lists
     */
    @Composable
    fun rememberStaggeredFloat(
        targetValue: Float,
        itemIndex: Int,
        staggerDelayMs: Int = 100
    ): State<Float> {
        return animateFloatAsState(
            targetValue = targetValue,
            animationSpec = tween(
                durationMillis = 600,
                delayMillis = itemIndex * staggerDelayMs,
                easing = EaseOutCubic
            ),
            label = "staggered_animation_$itemIndex"
        )
    }
    
    /**
     * Shared entrance animation specs for consistency
     */
    object EntranceSpecs {
        fun slideInFromBottom(delayMs: Int = 0): EnterTransition = slideInVertically(
            animationSpec = tween(600, delayMs, EaseOutCubic),
            initialOffsetY = { fullHeight: Int -> fullHeight / 3 }
        )
        
        fun slideInFromRight(delayMs: Int = 0): EnterTransition = slideInHorizontally(
            animationSpec = tween(600, delayMs, EaseOutCubic),
            initialOffsetX = { fullWidth: Int -> fullWidth / 2 }
        )
        
        fun fadeInWithScale(delayMs: Int = 0): EnterTransition = fadeIn(
            animationSpec = tween(600, delayMs)
        ) + scaleIn(
            animationSpec = tween(600, delayMs, EaseOutBack),
            initialScale = 0.8f
        )
    }
    
    /**
     * Shared exit animation specs
     */
    object ExitSpecs {
        fun fadeOutWithScale(): ExitTransition = fadeOut(
            animationSpec = tween(300)
        ) + scaleOut(
            animationSpec = tween(300),
            targetScale = 0.8f
        )
        
        fun slideOutToBottom(): ExitTransition = slideOutVertically(
            animationSpec = tween(300),
            targetOffsetY = { fullHeight: Int -> fullHeight / 3 }
        )
    }
    
    /**
     * Performance-optimized easing curves
     */
    object OptimizedEasing {
        val FastOutSlowIn = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
        val FastOutLinearIn = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)
        val LinearOutSlowIn = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
        val StandardDecelerate = CubicBezierEasing(0.0f, 0.0f, 0.2f, 1.0f)
        val StandardAccelerate = CubicBezierEasing(0.3f, 0.0f, 1.0f, 1.0f)
    }
}