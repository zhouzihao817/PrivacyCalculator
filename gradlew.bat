@echo off
setlocal enabledelayedexpansion

set DIR=%~dp0
set DEFAULT_JVM_OPTS=-Xmx64m -Xms64m

rem Determine JAVA_EXE
if defined JAVA_HOME (
    set JAVA_EXE=%JAVA_HOME%\bin\java.exe
) else (
    set JAVA_EXE=java.exe
)

rem Run Gradle Wrapper
set CLASSPATH=%DIR%gradle\wrapper\gradle-wrapper.jar
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*

endlocal
