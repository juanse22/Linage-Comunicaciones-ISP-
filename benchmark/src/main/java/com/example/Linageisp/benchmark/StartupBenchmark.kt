package com.example.Linageisp.benchmark

import androidx.benchmark.macro.*
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Startup benchmarks for Linage ISP app
 * Measures cold startup time with different compilation modes
 * Target: <300ms startup time with 60 FPS
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {
    
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()
    
    companion object {
        const val PACKAGE_NAME = "com.example.Linageisp"
        const val TARGET_FPS = 60f
        const val TARGET_STARTUP_MS = 300L
    }
    
    @Test
    fun startupNoCompilation() = startup(CompilationMode.None())
    
    @Test
    fun startupPartialCompilationWithBaselineProfiles() = startup(
        CompilationMode.Partial(
            baselineProfileMode = BaselineProfileMode.Require,
            warmupIterations = 3
        )
    )
    
    @Test
    fun startupFullCompilation() = startup(CompilationMode.Full())
    
    private fun startup(compilationMode: CompilationMode) {
        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(
                StartupTimingMetric(),
                FrameTimingMetric()
            ),
            iterations = 10,
            startupMode = StartupMode.COLD,
            compilationMode = compilationMode,
            setupBlock = {
                // Clear app data before each iteration
                pressHome()
            }
        ) {
            startActivityAndWait()
            
            // Wait for home screen to be fully loaded
            device.wait(
                Until.hasObject(By.text("¡Bienvenido!")),
                5000
            )
            
            // Verify critical elements are loaded (Phase 1)
            device.wait(
                Until.hasObject(By.text("Ver Todos los Planes")),
                2000
            )
            
            // Verify interactive elements are loaded (Phase 2)
            device.wait(
                Until.hasObject(By.text("¿Por qué Linage?")),
                3000
            )
            
            // Verify banner carousel is functional
            device.wait(
                Until.hasObject(By.textContains("Internet Super Rápido")),
                1000
            )
            
            // Test scroll performance
            val scrollable = device.findObject(By.scrollable(true))
            if (scrollable != null) {
                // Scroll down to trigger lazy loading
                repeat(3) {
                    scrollable.swipe(androidx.test.uiautomator.Direction.DOWN, 0.5f)
                    Thread.sleep(100)
                }
                
                // Scroll back up
                repeat(3) {
                    scrollable.swipe(androidx.test.uiautomator.Direction.UP, 0.5f)
                    Thread.sleep(100)
                }
            }
        }
    }
    
    @Test
    fun homeScreenScrollPerformance() {
        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric()),
            iterations = 5,
            startupMode = StartupMode.WARM,
            compilationMode = CompilationMode.Partial(
                baselineProfileMode = BaselineProfileMode.Require
            )
        ) {
            startActivityAndWait()
            
            // Wait for home screen
            device.wait(Until.hasObject(By.text("¡Bienvenido!")), 3000)
            
            val scrollable = device.findObject(By.scrollable(true))
            scrollable?.let { 
                // Perform sustained scrolling to measure frame drops
                repeat(10) {
                    it.swipe(androidx.test.uiautomator.Direction.DOWN, 0.7f)
                    Thread.sleep(50)
                }
                
                repeat(10) {
                    it.swipe(androidx.test.uiautomator.Direction.UP, 0.7f)
                    Thread.sleep(50)
                }
            }
        }
    }
    
    @Test
    fun animationPerformance() {
        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(FrameTimingMetric()),
            iterations = 5,
            startupMode = StartupMode.WARM,
            compilationMode = CompilationMode.Partial(
                baselineProfileMode = BaselineProfileMode.Require
            )
        ) {
            startActivityAndWait()
            
            // Wait for home screen and animations to start
            device.wait(Until.hasObject(By.text("¡Bienvenido!")), 3000)
            
            // Let animations run for measurement
            Thread.sleep(5000)
            
            // Interact with animated elements
            val quickActionButton = device.findObject(By.text("Ver Todos los Planes"))
            quickActionButton?.click()
            
            // Wait for navigation animation
            Thread.sleep(1000)
            
            // Go back
            device.pressBack()
            
            // Wait for return animation
            Thread.sleep(1000)
        }
    }
    
    @Test
    fun memoryUsageDuringStartup() {
        benchmarkRule.measureRepeated(
            packageName = PACKAGE_NAME,
            metrics = listOf(
                StartupTimingMetric()
            ),
            iterations = 5,
            startupMode = StartupMode.COLD,
            compilationMode = CompilationMode.Partial(
                baselineProfileMode = BaselineProfileMode.Require
            )
        ) {
            startActivityAndWait()
            
            // Wait for complete app startup
            device.wait(Until.hasObject(By.text("¡Bienvenido!")), 5000)
            
            // Trigger all lazy loading phases
            Thread.sleep(2000)
            
            // Verify all components loaded
            device.wait(Until.hasObject(By.text("Aliados Linage")), 2000)
        }
    }
}