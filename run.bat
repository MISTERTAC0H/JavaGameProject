@echo off
rem Compiles and runs the game from the command line.
rem Set JAVAFX_HOME to your JavaFX SDK folder if it isn't in the default location below.
setlocal
cd /d "%~dp0"

if "%JAVAFX_HOME%"=="" set "JAVAFX_HOME=%USERPROFILE%\Downloads\javafx-sdk-17.0.15"
if not exist "%JAVAFX_HOME%\lib\javafx.controls.jar" (
    echo JavaFX SDK not found at "%JAVAFX_HOME%".
    echo Download JavaFX 17 from https://gluonhq.com/products/javafx/ and set JAVAFX_HOME to the extracted folder.
    exit /b 1
)

if exist build rmdir /s /q build
mkdir build\classes

javac -encoding UTF-8 --module-path "%JAVAFX_HOME%\lib" --add-modules javafx.controls -d build\classes src\game\*.java
if errorlevel 1 exit /b 1

xcopy /e /i /q /y src\resources build\classes\resources >nul
xcopy /e /i /q /y src\maps build\classes\maps >nul

java --module-path "%JAVAFX_HOME%\lib" --add-modules javafx.controls -cp build\classes game.Main %*
