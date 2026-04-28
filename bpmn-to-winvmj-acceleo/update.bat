@echo off
REM Script to sync Acceleo project to plugin and optionally commit/push to git
REM Usage: sync-acceleo.bat [commit_message]
REM Example: sync-acceleo.bat "Updated Acceleo templates"

setlocal enabledelayedexpansion

REM Define paths - adjust these to your project structure
set ACCELEO_PROJECT=.
set PLUGIN_PROJECT=..\bpmn-to-winvmj-plugin
set ACCELEO_MIRROR=%PLUGIN_PROJECT%\acceleo

REM Check if Acceleo project exists
if not exist "%ACCELEO_PROJECT%" (
    echo Error: Acceleo project not found at %ACCELEO_PROJECT%
    exit /b 1
)

REM Check if plugin project exists
if not exist "%PLUGIN_PROJECT%" (
    echo Error: Plugin project not found at %PLUGIN_PROJECT%
    exit /b 1
)

echo.
echo ========================================
echo Syncing Acceleo to Plugin...
echo ========================================
echo.

REM Create acceleo folder structure if it doesn't exist
if not exist "%ACCELEO_MIRROR%\src" mkdir "%ACCELEO_MIRROR%\src"

REM Copy source files (.mtl and .java query helpers)
echo Copying source files (.mtl and .java)...
xcopy /E /Y /I "%ACCELEO_PROJECT%\src\bpmn" "%ACCELEO_MIRROR%\src\bpmn" >nul
if errorlevel 1 (
    echo Warning: Some source files may not have copied successfully
) else (
    echo Source files copied successfully
)

echo.
echo ========================================
echo Sync Complete!
echo ========================================
echo.

REM Check if deploy flag is provided
if not "%1"=="-n" (
    echo No deploy flag provided. Files copied but not committed.
    echo To commit and push, run: sync-acceleo.bat -n deploy
    exit /b 0
)

if not "%2"=="deploy" (
    echo Invalid parameter. Use: sync-acceleo.bat -n deploy
    exit /b 1
)

REM Git operations
cd /d "%PLUGIN_PROJECT%"

echo.
echo ========================================
echo Committing changes to git...
echo ========================================
echo.

REM Check if git is available
git --version >nul 2>&1
if errorlevel 1 (
    echo Error: Git not found. Make sure git is installed and in your PATH.
    exit /b 1
)

REM Add changes
echo Adding changes...
git add acceleo/
if errorlevel 1 (
    echo Error: Failed to stage changes
    exit /b 1
)

REM Commit with default message
echo Committing with default message...
git commit -m "Sync Acceleo templates and generated code"
if errorlevel 1 (
    echo Error: Failed to commit. Changes may already be committed or no changes detected.
    exit /b 1
)

REM Push
echo Pushing to remote...
git push
if errorlevel 1 (
    echo Error: Failed to push. Check your git configuration and remote.
    exit /b 1
)

echo.
echo ========================================
echo Successfully synced and pushed!
echo ========================================
echo.

endlocal
exit /b 0