@echo off
echo ========================================
echo LINAGE ISP - PERFORMANCE VERIFICATION
echo ========================================
echo.

echo [1/5] Checking build configuration...
if exist "app\build.gradle.kts" (
    echo ✅ build.gradle.kts found
) else (
    echo ❌ build.gradle.kts missing
    exit /b 1
)

echo.
echo [2/5] Checking performance optimizers...
if exist "app\src\main\java\com\example\Linageisp\performance\ComposePerformanceOptimizer.kt" (
    echo ✅ ComposePerformanceOptimizer created
) else (
    echo ❌ ComposePerformanceOptimizer missing
)

if exist "app\src\main\java\com\example\Linageisp\performance\DeviceTierOptimizer.kt" (
    echo ✅ DeviceTierOptimizer created
) else (
    echo ❌ DeviceTierOptimizer missing  
)

if exist "app\src\main\java\com\example\Linageisp\performance\FirebaseOptimizer.kt" (
    echo ✅ FirebaseOptimizer created
) else (
    echo ❌ FirebaseOptimizer missing
)

if exist "app\src\main\java\com\example\Linageisp\performance\AppStartupOptimizer.kt" (
    echo ✅ AppStartupOptimizer created
) else (
    echo ❌ AppStartupOptimizer missing
)

echo.
echo [3/5] Checking removed assets...
if not exist "app\src\main\res\drawable\netflix.webp" (
    echo ✅ netflix.webp removed
) else (
    echo ❌ netflix.webp still exists
)

if not exist "app\src\main\res\drawable\win_sports.webp" (
    echo ✅ win_sports.webp removed
) else (
    echo ❌ win_sports.webp still exists
)

echo.
echo [4/5] Checking ProGuard rules...
if exist "app\proguard-rules.pro" (
    echo ✅ ProGuard rules configured
) else (
    echo ❌ ProGuard rules missing
)

echo.
echo [5/5] Checking application integration...
findstr /c:"AppStartupOptimizer" "app\src\main\java\com\example\Linageisp\LinageApplication.kt" >nul
if %errorlevel%==0 (
    echo ✅ Application optimizations integrated
) else (
    echo ❌ Application optimizations not integrated
)

echo.
echo ========================================
echo PERFORMANCE VERIFICATION COMPLETE
echo ========================================
echo.
echo Next steps:
echo 1. Run: gradlew assembleRelease
echo 2. Test on real devices
echo 3. Use Android Studio Profiler
echo 4. Monitor performance metrics
echo.
pause