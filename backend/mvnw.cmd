@echo off
where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    mvn %*
) else (
    if exist "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.3\plugins\maven\lib\maven3\bin\mvn.cmd" (
        "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.3\plugins\maven\lib\maven3\bin\mvn.cmd" %*
    ) else (
        echo "Maven executable not found in PATH or standard directory."
        exit /b 1
    )
)
