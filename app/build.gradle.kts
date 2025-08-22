plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.Linageisp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.Linageisp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Optimization: Enable vector drawable support
        vectorDrawables.useSupportLibrary = true
        
        // Optimization: Enable build config fields
        buildConfigField("boolean", "ENABLE_PERFORMANCE_MONITORING", "true")
        buildConfigField("boolean", "ENABLE_LOGGING", "true")
        
        // Gemini API Key
        val apiKey = project.findProperty("GEMINI_API_KEY") as String? ?: "AIzaSyBiqDkrvC6tGkx3t3rUytklWHuwgbrTsBo"
        buildConfigField("String", "GEMINI_KEY", "\"$apiKey\"")
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            
            // Enable Compose compiler reports for debugging
            buildConfigField("boolean", "COMPOSE_COMPILER_REPORTS", "true")
        }
        
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            // Performance optimizations for release
            buildConfigField("boolean", "COMPOSE_COMPILER_REPORTS", "false")
            buildConfigField("boolean", "ENABLE_PERFORMANCE_MONITORING", "false")
            buildConfigField("boolean", "ENABLE_LOGGING", "false")
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        
        // Optimization: Enable core library desugaring for better compatibility
        isCoreLibraryDesugaringEnabled = true
    }
    
    kotlinOptions {
        jvmTarget = "11"
        
        // Compose compiler optimizations
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
            "-Xjvm-default=all",
            
            // Compose compiler performance optimizations (removed deprecated flags)
        )
    }
    
    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = false
        dataBinding = false
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    
    // ===== SOLUCION CRITICA: ERROR 16KB ALIGNMENT =====
    // Soluciona problemas de compatibilidad con dispositivos modernos
    // EXCLUYE ARCHIVOS ESPECÍFICOS QUE CAUSAN EL ERROR 16KB
    packaging {
        // Librerías JNI que causan conflictos de 16KB alignment (sintaxis moderna)
        jniLibs {
            pickFirsts += setOf(
                "**/libc++_shared.so",
                "**/libchromium_android_linker.so"
            )
            excludes += setOf(
                "lib/arm64-v8a/libbenchmarkNative.so",
                "lib/arm64-v8a/libtracing_perfetto.so",
                "**/arm64-v8a/libchromium_android_linker.so",
                "**/x86_64/libchromium_android_linker.so",
                "**/x86_64/libbenchmarkNative.so",
                "**/x86_64/libtracing_perfetto.so",
                "**/*benchmark*",
                "**/*perfetto*"
            )
        }
        
        // ARCHIVOS ESPECÍFICOS DEL ERROR 16KB - EXCLUSIÓN COMPLETA (sintaxis moderna)
        resources {
            excludes += setOf(
                // EXACTOS ARCHIVOS QUE CAUSAN EL ERROR - TODOS LOS ARCHITECTURAS
                "assets/trace_processor_shell_aarch64",
                "assets/trace_processor_shell_x86_64", 
                "assets/trace_processor_shell_arm",
                "assets/trace_processor_shell_x86",
                "assets/tracebox_aarch64",
                "assets/tracebox_x86_64",
                "assets/tracebox_arm", 
                "assets/tracebox_x86",
                
                // PATRONES ADICIONALES PARA ASEGURAR EXCLUSIÓN TOTAL
                "**/trace_processor_shell_*",
                "**/tracebox_*", 
                "**/*trace_processor*",
                "**/*tracebox*",
                "**/assets/trace_processor*",
                "**/assets/tracebox*",
                "trace_processor_shell_*",
                "tracebox_*",
                
                // EXCLUSIONES META-INF
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/ASL2.0",
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/*.kotlin_module",
                "META-INF/gradle/incremental.annotation.processors"
            )
        }
    }
    
    // Optimization: Enable bundle configuration
    bundle {
        language {
            enableSplit = false
        }
        density {
            enableSplit = true
        }
        abi {
            enableSplit = true
        }
    }
    
    // Optimization: Configure test options
    testOptions {
        unitTests.isReturnDefaultValues = true
        animationsDisabled = true
    }
}

dependencies {

    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    
    // Compose BOM and UI dependencies
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    
    // Latest Compose BOM for better performance
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui-util")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")
    
    // HTTP client and HTML parsing
    implementation("org.jsoup:jsoup:1.17.2")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    
    // Coroutines with performance optimizations
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")
    
    // ViewModel and Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-process:2.7.0")
    
    // Performance & Memory Optimization (benchmark removido por problema 16KB)
    // implementation("androidx.metrics:metrics-performance:1.0.0-alpha04")
    implementation("androidx.tracing:tracing:1.2.0")
    implementation("androidx.work:work-runtime-ktx:2.9.1")
    // implementation("androidx.benchmark:benchmark-macro-junit4:1.2.2")  // CAUSA ERROR 16KB
    implementation("androidx.profileinstaller:profileinstaller:1.3.1")
    
    // Image loading optimizations with Coil
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("io.coil-kt:coil-gif:2.5.0")
    implementation("io.coil-kt:coil-video:2.5.0")
    
    
    // Firebase BoM and dependencies
    implementation(platform("com.google.firebase:firebase-bom:34.1.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-perf")
    implementation("com.google.firebase:firebase-config")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-functions")
    implementation("com.google.firebase:firebase-inappmessaging-display")
    
    // Firebase Genkit AI and Gemini
    implementation("com.google.ai.client.generativeai:generativeai:0.7.0")
    
    // Markdown support for chat
    implementation("io.noties.markwon:core:4.6.2")
    implementation("io.noties.markwon:ext-strikethrough:4.6.2")
    implementation("io.noties.markwon:ext-tables:4.6.2")
    
    // Additional Compose animations for chat UI
    implementation("androidx.compose.animation:animation-graphics:1.5.4")
    
    // Hilt dependency injection
    implementation("com.google.dagger:hilt-android:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    kapt("com.google.dagger:hilt-compiler:2.48")
    
    // HTTP client for AI API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Core library desugaring
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    
    // Testing dependencies
    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    testImplementation("androidx.compose.ui:ui-test-junit4")
    testImplementation("io.mockk:mockk:1.13.8")
    
    // Android testing
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation("androidx.compose.ui:ui-test-manifest")
    // androidTestImplementation("androidx.benchmark:benchmark-macro-junit4:1.2.2") // CAUSA ERROR 16KB
    
    // Debug dependencies
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}