@echo off
REM ================================================================================================
REM LINAGE ISP - PERFORMANCE VERIFICATION SCRIPT
REM Automated testing and verification of all performance optimizations
REM ================================================================================================

echo.
echo ================================================================================================
echo LINAGE ISP PERFORMANCE VERIFICATION
echo Target: 60 FPS, <300ms startup, <30MB RAM
echo ================================================================================================
echo.

REM Set variables
set PACKAGE_NAME=com.example.Linageisp
set ADB=adb
set DEVICE_CONNECTED=0

REM Check if ADB is available
%ADB% version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] ADB not found in PATH. Please install Android SDK.
    exit /b 1
)

REM Check if device is connected
%ADB% devices | findstr "device" >nul
if %errorlevel% equ 0 (
    set DEVICE_CONNECTED=1
    echo [INFO] Android device detected
) else (
    echo [WARNING] No Android device detected. Some tests will be skipped.
)

echo.
echo ------------------------------------------------------------------------------------------------
echo 1. BUILD VERIFICATION
echo ------------------------------------------------------------------------------------------------

echo [INFO] Building release APK with optimizations...
call gradlew assembleRelease
if %errorlevel% neq 0 (
    echo [ERROR] Release build failed
    exit /b 1
)

echo [SUCCESS] Release APK built successfully

echo.
echo ------------------------------------------------------------------------------------------------
echo 2. STATIC CODE ANALYSIS
echo ------------------------------------------------------------------------------------------------

echo [INFO] Running lint analysis...
call gradlew lintRelease
if %errorlevel% neq 0 (
    echo [WARNING] Lint found issues. Check lint-results-release.html
) else (
    echo [SUCCESS] Lint analysis passed
)

echo.
echo ------------------------------------------------------------------------------------------------
echo 3. APK SIZE ANALYSIS
echo ------------------------------------------------------------------------------------------------

if exist "app\build\outputs\apk\release\app-release.apk" (
    for %%I in ("app\build\outputs\apk\release\app-release.apk") do (
        set /a apk_size_mb=%%~zI/1024/1024
        echo [INFO] Release APK size: !apk_size_mb!MB
        if !apk_size_mb! leq 50 (
            echo [SUCCESS] APK size is within target (<=50MB)
        ) else (
            echo [WARNING] APK size exceeds target (50MB)
        )
    )
) else (
    echo [ERROR] Release APK not found
)

if %DEVICE_CONNECTED% equ 0 goto skip_device_tests

echo.
echo ------------------------------------------------------------------------------------------------
echo 4. DEVICE PERFORMANCE TESTING
echo ------------------------------------------------------------------------------------------------

echo [INFO] Installing release APK...
%ADB% install -r "app\build\outputs\apk\release\app-release.apk"
if %errorlevel% neq 0 (
    echo [ERROR] Failed to install APK
    goto skip_device_tests
)

echo [INFO] Starting cold startup time measurement...
%ADB% shell am force-stop %PACKAGE_NAME%
%ADB% shell am start -W -n %PACKAGE_NAME%/.MainActivity > startup_time.tmp
for /f "tokens=2 delims=:" %%a in ('findstr "TotalTime" startup_time.tmp') do (
    set startup_time=%%a
    set startup_time=!startup_time: =!
)

echo [INFO] Cold startup time: %startup_time%ms
if %startup_time% leq 300 (
    echo [SUCCESS] Startup time within target (<=300ms)
) else (
    echo [WARNING] Startup time exceeds target (300ms)
)

echo.
echo [INFO] Measuring memory usage...
timeout /t 3 >nul
%ADB% shell dumpsys meminfo %PACKAGE_NAME% | findstr "TOTAL" > memory_usage.tmp
for /f "tokens=2" %%a in ('type memory_usage.tmp') do (
    set /a memory_mb=%%a/1024
    echo [INFO] Memory usage: !memory_mb!MB
    if !memory_mb! leq 30 (
        echo [SUCCESS] Memory usage within target (<=30MB)
    ) else (
        echo [WARNING] Memory usage exceeds target (30MB)
    )
    goto memory_done
)
:memory_done

echo.
echo [INFO] Checking for ANRs and crashes...
%ADB% shell dumpsys activity processes | findstr "ANR\|CRASH" > issues.tmp
if exist issues.tmp (
    for /f %%a in ('type issues.tmp ^| find /c /v ""') do set line_count=%%a
    if !line_count! gtr 0 (
        echo [WARNING] Found ANRs or crashes:
        type issues.tmp
    ) else (
        echo [SUCCESS] No ANRs or crashes detected
    )
) else (
    echo [SUCCESS] No ANRs or crashes detected
)

echo.
echo [INFO] Measuring FPS performance...
%ADB% shell dumpsys gfxinfo %PACKAGE_NAME% | findstr "Janky frames:" > fps.tmp
for /f "tokens=3,5" %%a in ('type fps.tmp') do (
    set janky_frames=%%a
    set total_frames=%%b
    if defined total_frames if !total_frames! gtr 0 (
        set /a jank_percentage=!janky_frames!*100/!total_frames!
        echo [INFO] Janky frames: !jank_percentage!%%
        if !jank_percentage! leq 5 (
            echo [SUCCESS] Frame drops within target (<=5%%)
        ) else (
            echo [WARNING] Excessive frame drops (!jank_percentage!%%)
        )
    )
)

:skip_device_tests

echo.
echo ------------------------------------------------------------------------------------------------
echo 5. BENCHMARK TESTS (if available)
echo ------------------------------------------------------------------------------------------------

if exist "benchmark" (
    echo [INFO] Running macrobenchmark tests...
    call gradlew :benchmark:connectedBenchmarkAndroidTest
    if %errorlevel% equ 0 (
        echo [SUCCESS] Benchmark tests passed
    ) else (
        echo [WARNING] Benchmark tests failed or no device available
    )
) else (
    echo [INFO] Benchmark module not found, skipping benchmark tests
)

echo.
echo ------------------------------------------------------------------------------------------------
echo 6. BASELINE PROFILE VERIFICATION
echo ------------------------------------------------------------------------------------------------

if exist "app\src\main\baseline-prof.txt" (
    echo [SUCCESS] Baseline profile found
    for /f %%a in ('type "app\src\main\baseline-prof.txt" ^| find /c "HSPLcom/example/Linageisp"') do (
        echo [INFO] Baseline profile contains %%a app-specific methods
        if %%a gtr 50 (
            echo [SUCCESS] Comprehensive baseline profile (%%a methods)
        ) else (
            echo [WARNING] Limited baseline profile coverage (%%a methods)
        )
    )
) else (
    echo [WARNING] Baseline profile not found
)

echo.
echo ------------------------------------------------------------------------------------------------
echo 7. PROGUARD OPTIMIZATION VERIFICATION
echo ------------------------------------------------------------------------------------------------

if exist "app\build\outputs\mapping\release\mapping.txt" (
    echo [SUCCESS] ProGuard mapping file found (code obfuscation active)
    for /f %%a in ('type "app\build\outputs\mapping\release\mapping.txt" ^| find /c "->"') do (
        echo [INFO] %%a classes/methods obfuscated
    )
) else (
    echo [WARNING] ProGuard mapping file not found (minification may not be active)
)

echo.
echo ------------------------------------------------------------------------------------------------
echo 8. FINAL PERFORMANCE SUMMARY
echo ------------------------------------------------------------------------------------------------

echo.
echo Performance Test Results:
echo - Cold Startup: %startup_time%ms (Target: <=300ms)
if defined memory_mb echo - Memory Usage: %memory_mb%MB (Target: <=30MB)
if defined jank_percentage echo - Frame Drops: %jank_percentage%% (Target: <=5%)
echo - APK Size: %apk_size_mb%MB (Target: <=50MB)
echo.

REM Calculate overall score
set /a score=0
if %startup_time% leq 300 set /a score+=25
if defined memory_mb if %memory_mb% leq 30 set /a score+=25
if defined jank_percentage if %jank_percentage% leq 5 set /a score+=25
if %apk_size_mb% leq 50 set /a score+=25

echo Overall Performance Score: %score%/100

if %score% geq 90 (
    echo [EXCELLENT] Performance targets exceeded! 🎉
) else if %score% geq 75 (
    echo [GOOD] Performance targets mostly met ✅
) else if %score% geq 50 (
    echo [FAIR] Some performance issues need attention ⚠️
) else (
    echo [POOR] Significant performance issues detected ❌
)

echo.
echo ================================================================================================
echo VERIFICATION COMPLETE
echo ================================================================================================

REM Cleanup temporary files
if exist startup_time.tmp del startup_time.tmp
if exist memory_usage.tmp del memory_usage.tmp
if exist issues.tmp del issues.tmp
if exist fps.tmp del fps.tmp

pause