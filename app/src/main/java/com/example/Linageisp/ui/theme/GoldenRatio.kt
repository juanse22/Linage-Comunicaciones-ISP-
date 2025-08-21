package com.example.Linageisp.ui.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.pow

/**
 * Golden Ratio design system for mathematically perfect proportions
 * Based on φ (phi) = 1.618... - the divine proportion found in nature
 */
object GoldenRatio {
    
    // Mathematical constants
    const val PHI = 1.618f
    const val PHI_INVERSE = 0.618f  // 1/φ
    const val PHI_SQUARED = 2.618f  // φ²
    
    // Base unit for all measurements (following 8dp grid system)
    private const val BASE_UNIT = 8f
    
    // === SPACING SYSTEM ===
    // Each size is the previous multiplied by PHI for harmonic progression
    
    val SPACING_XXXS = (BASE_UNIT * 0.5f).dp                    // 4dp
    val SPACING_XXS = BASE_UNIT.dp                              // 8dp  
    val SPACING_XS = (BASE_UNIT * PHI_INVERSE).dp               // 5dp (8 * 0.618)
    val SPACING_SM = (BASE_UNIT * 1.0f).dp                      // 8dp
    val SPACING_MD = (BASE_UNIT * PHI_INVERSE * 2).dp           // 10dp
    val SPACING_LG = (BASE_UNIT * PHI).dp                       // 13dp (8 * 1.618)
    val SPACING_XL = (BASE_UNIT * PHI * PHI_INVERSE * 3).dp     // 15dp
    val SPACING_XXL = (BASE_UNIT * PHI * 2).dp                  // 26dp (13 * 2)
    val SPACING_XXXL = (BASE_UNIT * PHI.pow(2)).dp              // 21dp (8 * 2.618)
    val SPACING_MEGA = (BASE_UNIT * PHI.pow(2) * 2).dp          // 42dp
    
    // === CARD DIMENSIONS ===
    // Optimized for mobile screens with golden ratio proportions
    
    val CARD_WIDTH_COMPACT = (200f).dp
    val CARD_HEIGHT_COMPACT = (200f * PHI).dp                   // ~324dp
    
    val CARD_WIDTH_MEDIUM = (280f).dp  
    val CARD_HEIGHT_MEDIUM = (280f * PHI).dp                    // ~453dp
    
    val CARD_WIDTH_LARGE = (320f).dp
    val CARD_HEIGHT_LARGE = (320f * PHI).dp                     // ~518dp
    
    // Special card ratios for different content types
    val CARD_WIDTH_PLAN = CARD_WIDTH_MEDIUM
    val CARD_HEIGHT_PLAN = (CARD_WIDTH_MEDIUM.value * PHI_INVERSE).dp  // Shorter for plan cards
    
    val CARD_WIDTH_CATEGORY = -1.dp  // Full width
    val CARD_HEIGHT_CATEGORY = (160f * PHI).dp                  // ~259dp
    
    // === TYPOGRAPHY SCALE ===
    // Font sizes following golden ratio progression
    
    val TEXT_SIZE_CAPTION = (10f * PHI_INVERSE + 2).sp          // ~8.38sp
    val TEXT_SIZE_BODY_SMALL = (12f * PHI_INVERSE + 2).sp       // ~9.42sp  
    val TEXT_SIZE_BODY = (14f * PHI_INVERSE + 2).sp             // ~10.65sp
    val TEXT_SIZE_BODY_LARGE = (16f * PHI_INVERSE + 2).sp       // ~11.89sp
    val TEXT_SIZE_TITLE = (18f * PHI_INVERSE + 4).sp            // ~15.12sp
    val TEXT_SIZE_TITLE_LARGE = (20f * PHI).sp                  // ~32.36sp
    val TEXT_SIZE_HEADLINE = (24f * PHI).sp                     // ~38.83sp
    val TEXT_SIZE_DISPLAY = (32f * PHI).sp                      // ~51.78sp
    
    // === CORNER RADIUS SYSTEM ===
    // Harmonious rounded corners
    
    val RADIUS_NONE = 0.dp
    val RADIUS_XS = (BASE_UNIT * 0.5f).dp                       // 4dp
    val RADIUS_SM = BASE_UNIT.dp                                // 8dp
    val RADIUS_MD = (BASE_UNIT * PHI_INVERSE + 4).dp            // ~9dp
    val RADIUS_LG = (BASE_UNIT * PHI).dp                        // ~13dp
    val RADIUS_XL = (BASE_UNIT * PHI * 1.5f).dp                 // ~19dp
    val RADIUS_XXL = (BASE_UNIT * PHI * 2).dp                   // ~26dp
    val RADIUS_ROUNDED = (BASE_UNIT * PHI.pow(2)).dp            // ~21dp
    val RADIUS_CIRCLE = 9999.dp
    
    // === ELEVATION SYSTEM ===
    // Shadow depths following golden ratio
    
    val ELEVATION_NONE = 0.dp
    val ELEVATION_XS = (2f * PHI_INVERSE).dp                    // ~1.24dp
    val ELEVATION_SM = (4f * PHI_INVERSE).dp                    // ~2.47dp
    val ELEVATION_MD = (6f * PHI_INVERSE + 2).dp                // ~5.71dp
    val ELEVATION_LG = (8f * PHI).dp                            // ~12.94dp
    val ELEVATION_XL = (12f * PHI).dp                           // ~19.42dp
    val ELEVATION_XXL = (16f * PHI).dp                          // ~25.89dp
    
    // === ANIMATION TIMING ===
    // Duration following golden ratio for smooth, natural timing
    
    const val DURATION_INSTANT = 0
    const val DURATION_FAST = (200 * PHI_INVERSE).toInt()       // ~124ms
    const val DURATION_NORMAL = (300 * PHI_INVERSE + 100).toInt() // ~285ms  
    const val DURATION_SLOW = (400 * PHI).toInt()               // ~647ms
    const val DURATION_SLOWER = (600 * PHI).toInt()             // ~971ms
    const val DURATION_SLOWEST = (800 * PHI).toInt()            // ~1295ms
    
    // === COMPONENT-SPECIFIC CONSTANTS ===
    
    // Header proportions
    val HEADER_HEIGHT_COMPACT = (80f * PHI).dp                  // ~129dp
    val HEADER_HEIGHT_MEDIUM = (100f * PHI).dp                  // ~162dp
    val HEADER_HEIGHT_LARGE = (120f * PHI).dp                   // ~194dp
    
    // Button dimensions
    val BUTTON_HEIGHT_COMPACT = (32f * PHI_INVERSE + 8).dp      // ~28dp
    val BUTTON_HEIGHT_MEDIUM = (40f * PHI_INVERSE + 8).dp       // ~33dp
    val BUTTON_HEIGHT_LARGE = (48f * PHI_INVERSE + 8).dp        // ~38dp
    
    // Icon sizes
    val ICON_SIZE_XS = (16f * PHI_INVERSE + 4).dp               // ~14dp
    val ICON_SIZE_SM = (20f * PHI_INVERSE + 4).dp               // ~16dp
    val ICON_SIZE_MD = (24f * PHI_INVERSE + 4).dp               // ~19dp
    val ICON_SIZE_LG = (32f * PHI_INVERSE + 4).dp               // ~24dp
    val ICON_SIZE_XL = (40f * PHI).dp                           // ~65dp
    
    // === LAYOUT PROPORTIONS ===
    
    // Content width ratios for different screen sections
    const val CONTENT_RATIO_MAIN = PHI_INVERSE              // 0.618 - Main content takes larger portion
    const val CONTENT_RATIO_SIDEBAR = (1f - PHI_INVERSE)    // 0.382 - Sidebar takes smaller portion
    
    // Aspect ratios for media content
    const val ASPECT_RATIO_GOLDEN = PHI                     // 1.618:1 - Perfect golden rectangle
    const val ASPECT_RATIO_PHOTO = 1.5f                     // 3:2 - Standard photo ratio
    const val ASPECT_RATIO_VIDEO = 1.777f                   // 16:9 - Standard video ratio
    
    // Grid system
    const val GRID_COLUMNS_MOBILE = 2
    const val GRID_COLUMNS_TABLET = 3
    const val GRID_COLUMNS_DESKTOP = (3 * PHI).toInt()      // ~5 columns
    
    // === HELPER FUNCTIONS ===
    
    /**
     * Calculate golden ratio progression for any base value
     */
    fun goldenProgression(base: Float, step: Int): Float {
        return base * PHI.pow(step)
    }
    
    /**
     * Calculate inverse golden ratio progression  
     */
    fun inverseGoldenProgression(base: Float, step: Int): Float {
        return base * PHI_INVERSE.pow(step)
    }
    
    /**
     * Get harmonious spacing for any index
     */
    fun getHarmonicSpacing(index: Int): androidx.compose.ui.unit.Dp {
        return (BASE_UNIT * goldenProgression(1f, index)).dp
    }
    
    /**
     * Validate if a ratio is close to golden ratio (within tolerance)
     */
    fun isGoldenRatio(ratio: Float, tolerance: Float = 0.1f): Boolean {
        return kotlin.math.abs(ratio - PHI) < tolerance
    }
    
    /**
     * Convert any measurement to golden ratio proportional measurement
     */
    fun toGoldenRatio(value: Float): Float {
        return value * PHI
    }
    
    fun toInverseGoldenRatio(value: Float): Float {
        return value * PHI_INVERSE
    }
    
    // === PLAN-SPECIFIC CONSTANTS ===
    // Optimized dimensions specifically for plan cards and categories
    
    // Plan category cards (expandable)
    val CATEGORY_HEADER_HEIGHT = (140f * PHI).dp                   // ~226dp - Perfect for category headers
    val CATEGORY_CARD_RADIUS = (BASE_UNIT * PHI * 1.5f).dp         // ~20dp - Elegant rounded corners
    val CATEGORY_ICON_SIZE = (BASE_UNIT * PHI * 3).dp              // ~39dp - Prominent category icons
    
    // Individual plan cards within categories
    val PLAN_CARD_WIDTH = (260f).dp                                // Optimized for mobile
    val PLAN_CARD_HEIGHT = (PLAN_CARD_WIDTH.value * PHI).dp        // ~421dp - Golden rectangle
    val PLAN_HEADER_HEIGHT = (PLAN_CARD_HEIGHT.value * PHI_INVERSE).dp  // ~260dp - Header section
    val PLAN_CONTENT_HEIGHT = (PLAN_CARD_HEIGHT.value * PHI_INVERSE * PHI_INVERSE).dp  // ~161dp - Content section
    
    // Plan typography
    val PLAN_TITLE_SIZE = (16f * PHI_INVERSE + 6).sp               // ~16sp - Plan name
    val PLAN_PRICE_SIZE = (20f * PHI).sp                           // ~32sp - Price emphasis
    val PLAN_SPEED_SIZE = (12f * PHI_INVERSE + 4).sp               // ~11sp - Speed info
    val PLAN_BENEFIT_SIZE = (10f * PHI_INVERSE + 3).sp             // ~9sp - Benefit text
    
    // Plan button
    val PLAN_BUTTON_HEIGHT = (BASE_UNIT * PHI * 2.5f).dp           // ~32dp - CTA button
    val PLAN_BUTTON_RADIUS = (BASE_UNIT * PHI_INVERSE + 2).dp      // ~7dp - Button corners
    
    // Category expansion animation timing
    const val CATEGORY_EXPAND_DURATION = (300 * PHI).toInt()       // ~485ms - Smooth expansion
    const val PLAN_STAGGER_DELAY = (100 * PHI_INVERSE).toInt()     // ~62ms - Staggered plan appearance
    
    // Plan spacing within categories
    val PLAN_HORIZONTAL_SPACING = (BASE_UNIT * PHI).dp             // ~13dp - Between plan cards
    val PLAN_VERTICAL_SPACING = (BASE_UNIT * PHI * 1.5f).dp        // ~20dp - Category separation
}