@echo off
cd /d "%~dp0"
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0Scripts\Run.ps1"
if errorlevel 1 pause
