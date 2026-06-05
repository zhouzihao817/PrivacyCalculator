@echo off
REM Privacy Calculator - 手动构建APK脚本（不依赖Gradle）
REM 使用Android SDK命令行工具直接构建

set SDK_DIR=C:\Users\Administrator\android-sdk
set BUILD_TOOLS=%SDK_DIR%\build-tools\34.0.0
set PROJECT_DIR=C:\Users\Administrator\Desktop\PrivacyCalculator-Android
set APP_DIR=%PROJECT_DIR%\app
set SRC_DIR=%APP_DIR%\src\main\java
set RES_DIR=%APP_DIR%\src\main\res
set MANIFEST=%APP_DIR%\src\main\AndroidManifest.xml
set OUT_DIR=%PROJECT_DIR%\build\outputs

echo ========================================
echo  Privacy Calculator - 手动构建APK
echo ========================================
echo.

REM 设置JAVA_HOME
set JAVA_HOME=C:\Users\Administrator\jdk17.0.19_10
set PATH=%JAVA_HOME%\bin;%PATH%

echo [1/7] 创建输出目录...
if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"
if not exist "%OUT_DIR%\tmp" mkdir "%OUT_DIR%\tmp"
if not exist "%OUT_DIR%\classes" mkdir "%OUT_DIR%\classes"

echo [2/7] 生成R.java（资源索引）...
if not exist "%OUT_DIR%\gen" mkdir "%OUT_DIR%\gen"
"%BUILD_TOOLS%\aapt.exe" package -f -m -S "%RES_DIR%" -I "%SDK_DIR%\platforms\android-34\android.jar" -M "%MANIFEST%" -J "%OUT_DIR%\gen" --auto-add-overlay
if errorlevel 1 goto :error

echo [3/7] 编译Java源文件...
dir /s /b "%SRC_DIR%\*.java" > "%OUT_DIR%\sources.txt"
dir /s /b "%OUT_DIR%\gen\*.java" >> "%OUT_DIR%\sources.txt"
"%JAVA_HOME%\bin\javac.exe" -d "%OUT_DIR%\classes" -classpath "%SDK_DIR%\platforms\android-34\android.jar" -sourcepath "%SRC_DIR%;%OUT_DIR%\gen" @""%OUT_DIR%\sources.txt""
if errorlevel 1 goto :error

echo [4/7] 转换为DEX文件...
"%BUILD_TOOLS%\dx.exe" --dex --output="%OUT_DIR%\classes.dex" "%OUT_DIR%\classes"
if errorlevel 1 goto :error

echo [5/7] 打包APK（未签名）...
"%BUILD_TOOLS%\aapt.exe" package -f -S "%RES_DIR%" -I "%SDK_DIR%\platforms\android-34\android.jar" -M "%MANIFEST%" -F "%OUT_DIR%\app-unsigned.apk" -F "%OUT_DIR%\classes.dex"
if errorlevel 1 goto :error

echo [6/7] 生成签名密钥（如不存在）...
if not exist "%OUT_DIR%\debug.keystore" (
    "%JAVA_HOME%\bin\keytool.exe" -genkey -v -keystore "%OUT_DIR%\debug.keystore" -storepass android -alias androiddebugkey -keypass android -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Android Debug,O=Android,C=US"
)
if errorlevel 1 goto :error

echo [7/7] 签名APK...
"%BUILD_TOOLS%\apksigner.bat" sign --ks "%OUT_DIR%\debug.keystore" --ks-pass pass:android "%OUT_DIR%\app-unsigned.apk"
if errorlevel 1 goto :error

echo.
echo ========================================
echo  构建成功！
echo  输出: %OUT_DIR%\app-unsigned.apk
echo ========================================
goto :end

:error
echo.
echo [错误] 构建失败！请检查上述错误信息。
pause
exit /b 1

:end
pause
