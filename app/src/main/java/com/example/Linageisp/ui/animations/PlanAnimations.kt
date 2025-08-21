package com.example.Linageisp.ui.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.example.Linageisp.ui.theme.GoldenRatio

/**
 * Animaciones específicas optimizadas para las tarjetas de planes
 * Utiliza proporción áurea para tiempos y transiciones naturales
 */
object PlanAnimations {
    
    /**
     * Animación de float optimizada para planes con duración basada en proporción áurea
     */
    @Composable
    fun rememberPlanFloat(
        initialValue: Float,
        targetValue: Float,
        durationMs: Int = GoldenRatio.CATEGORY_EXPAND_DURATION,
        easing: Easing = FastOutSlowInEasing
    ): State<Float> {
        return animateFloatAsState(
            targetValue = targetValue,
            animationSpec = tween(
                durationMillis = durationMs,
                easing = easing
            ),
            label = "plan_float_animation"
        )
    }
    
    /**
     * Animación de color para transiciones suaves en planes
     */
    @Composable
    fun rememberPlanColor(
        targetColor: Color,
        durationMs: Int = GoldenRatio.DURATION_NORMAL
    ): State<Color> {
        return animateColorAsState(
            targetValue = targetColor,
            animationSpec = tween(
                durationMillis = durationMs,
                easing = EaseInOutCubic
            ),
            label = "plan_color_animation"
        )
    }
    
    /**
     * Spring animation optimizada para interacciones táctiles en plans
     */
    @Composable
    fun rememberPlanSpring(
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
            label = "plan_spring_animation"
        )
    }
    
    /**
     * Animación de expansión de categoría con timing basado en proporción áurea
     */
    @Composable
    fun categoryExpansionAnimation(
        isExpanded: Boolean
    ): State<Float> {
        return animateFloatAsState(
            targetValue = if (isExpanded) 180f else 0f,
            animationSpec = tween(
                durationMillis = GoldenRatio.CATEGORY_EXPAND_DURATION,
                easing = EaseInOutCubic
            ),
            label = "category_expansion"
        )
    }
    
    /**
     * Especificaciones de entrada para categorías de planes
     */
    object CategoryEntranceSpecs {
        fun slideInFromTop(delayMs: Int = 0): EnterTransition = slideInVertically(
            animationSpec = tween(
                durationMillis = GoldenRatio.CATEGORY_EXPAND_DURATION,
                delayMillis = delayMs,
                easing = EaseOutCubic
            ),
            initialOffsetY = { fullHeight -> -fullHeight / 3 }
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = GoldenRatio.CATEGORY_EXPAND_DURATION,
                delayMillis = delayMs
            )
        )
        
        fun expandWithFade(): EnterTransition = fadeIn(
            animationSpec = tween(GoldenRatio.CATEGORY_EXPAND_DURATION)
        ) + expandVertically(
            animationSpec = tween(
                durationMillis = GoldenRatio.CATEGORY_EXPAND_DURATION,
                easing = EaseInOutCubic
            )
        )
    }
    
    /**
     * Especificaciones de salida para categorías de planes
     */
    object CategoryExitSpecs {
        fun shrinkWithFade(): ExitTransition = fadeOut(
            animationSpec = tween(GoldenRatio.DURATION_NORMAL)
        ) + shrinkVertically(
            animationSpec = tween(
                durationMillis = GoldenRatio.DURATION_NORMAL,
                easing = EaseInOutCubic
            )
        )
    }
    
    /**
     * Especificaciones de entrada para tarjetas de plan individuales
     */
    object PlanCardEntranceSpecs {
        fun slideInFromRight(delayMs: Int = 0): EnterTransition = slideInHorizontally(
            animationSpec = tween(
                durationMillis = GoldenRatio.DURATION_SLOW,
                delayMillis = delayMs,
                easing = EaseOutCubic
            ),
            initialOffsetX = { fullWidth -> fullWidth / 2 }
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = GoldenRatio.DURATION_SLOW,
                delayMillis = delayMs
            )
        )
        
        fun scaleInWithFade(delayMs: Int = 0): EnterTransition = fadeIn(
            animationSpec = tween(
                durationMillis = GoldenRatio.DURATION_SLOW,
                delayMillis = delayMs
            )
        ) + scaleIn(
            animationSpec = tween(
                durationMillis = GoldenRatio.DURATION_SLOW,
                delayMillis = delayMs,
                easing = EaseOutBack
            ),
            initialScale = 0.8f
        )
    }
    
    /**
     * Animación staggered para múltiples planes en una categoría
     */
    @Composable
    fun rememberStaggeredPlanAnimation(
        isVisible: Boolean,
        itemIndex: Int,
        staggerDelayMs: Int = GoldenRatio.PLAN_STAGGER_DELAY
    ): State<Float> {
        return animateFloatAsState(
            targetValue = if (isVisible) 1f else 0f,
            animationSpec = tween(
                durationMillis = GoldenRatio.DURATION_SLOW,
                delayMillis = itemIndex * staggerDelayMs,
                easing = EaseOutCubic
            ),
            label = "staggered_plan_$itemIndex"
        )
    }
    
    /**
     * Animación de hover/press para tarjetas de plan
     */
    @Composable
    fun rememberPlanCardInteraction(
        isPressed: Boolean,
        pressScale: Float = 0.96f,
        normalScale: Float = 1f
    ): State<Float> {
        return animateFloatAsState(
            targetValue = if (isPressed) pressScale else normalScale,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessHigh
            ),
            label = "plan_card_interaction"
        )
    }
    
    /**
     * Animación de elevación para tarjetas de plan
     */
    @Composable
    fun rememberPlanCardElevation(
        isHovered: Boolean,
        normalElevation: Float = 4f,
        hoveredElevation: Float = 12f
    ): State<Float> {
        return animateFloatAsState(
            targetValue = if (isHovered) hoveredElevation else normalElevation,
            animationSpec = tween(
                durationMillis = GoldenRatio.DURATION_FAST,
                easing = EaseInOutCubic
            ),
            label = "plan_card_elevation"
        )
    }
}