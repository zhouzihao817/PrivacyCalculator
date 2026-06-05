@echo off
rem Gradle startup script for Windows
set DIR=%~dp0
set JAVA_OPTS=-Xmx64m -Xms64m

if not "%JAVA_HOME%" == "" goto okJavaHome
set JAVA_EXE=java.exe
%JAVA_EXE% -version
goto okJava

:okJava
set JAVA_EXE=%JAVA_HOME%\bin\java.exe

:okJavaHome
set CLASSPATH=%DIR%gradle\wrapper\gradle-wrapper.jar
"%JAVA_EXE%" -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
