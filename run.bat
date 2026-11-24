@echo off
title E-Commerce Logistics Hub Launcher
echo Starting Logistics Hub Desktop Application...
java -jar dist\LogisticsHub.jar
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo An error occurred while launching the application.
    pause
)
