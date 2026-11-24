@echo off
title E-Commerce Logistics Hub Launcher
echo Starting Logistics Hub Desktop Application...

if exist "LogisticsHub.jar" (
    java -jar LogisticsHub.jar
) else if exist "dist\LogisticsHub.jar" (
    java -jar dist\LogisticsHub.jar
) else (
    echo [ERROR] LogisticsHub.jar not found!
    echo Please make sure LogisticsHub.jar is in this folder.
    pause
    exit /b 1
)

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ============================================================
    echo An error occurred while launching the application.
    echo Make sure you have Java 17 or newer installed on your PC.
    echo You can download it for free from: https://adoptium.net/
    echo ============================================================
    pause
)
