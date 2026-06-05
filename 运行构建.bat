@echo off
REM 启动PowerShell构建脚本（绕过执行策略）
powershell -ExecutionPolicy Bypass -File "%~dp0build.ps1"
pause
