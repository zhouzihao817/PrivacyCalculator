# Privacy Calculator - PowerShell构建脚本（手动构建APK）
# 使用Android SDK命令行工具，不依赖Gradle

$SDK_DIR = "C:\Users\Administrator\android-sdk"
$BUILD_TOOLS = "$SDK_DIR\build-tools\34.0.0"
$PROJECT_DIR = "C:\Users\Administrator\Desktop\PrivacyCalculator-Android"
$APP_DIR = "$PROJECT_DIR\app"
$SRC_DIR = "$APP_DIR\src\main\java"
$RES_DIR = "$APP_DIR\src\main\res"
$MANIFEST = "$APP_DIR\src\main\AndroidManifest.xml"
$OUT_DIR = "$PROJECT_DIR\build\outputs"
$GEN_DIR = "$OUT_DIR\gen"
$CLASSES_DIR = "$OUT_DIR\classes"
$DEX_FILE = "$OUT_DIR\classes.dex"
$APK_UNSIGNED = "$OUT_DIR\app-unsigned.apk"
$APK_SIGNED = "$OUT_DIR\PrivacyCalculator-debug.apk"
$KEYSTORE = "$OUT_DIR\debug.keystore"

Write-Host "========================================"
Write-Host "  Privacy Calculator - 手动构建APK"
Write-Host "========================================"
Write-Host ""

# 设置JAVA_HOME
$env:JAVA_HOME = "C:\Users\Administrator\jdk17.0.19_10"
$env:PATH = "$env:JAVA_HOME\bin;" + $env:PATH

Write-Host "[1/8] 创建输出目录..."
if (-not (Test-Path $OUT_DIR)) { New-Item -ItemType Directory -Path $OUT_DIR -Force | Out-Null }
if (-not (Test-Path $GEN_DIR)) { New-Item -ItemType Directory -Path $GEN_DIR -Force | Out-Null }
if (-not (Test-Path $CLASSES_DIR)) { New-Item -ItemType Directory -Path $CLASSES_DIR -Force | Out-Null }

Write-Host "[2/8] 生成R.java（资源索引）..."
$ANDROID_JAR = "$SDK_DIR\platforms\android-34\android.jar"
& "$BUILD_TOOLS\aapt.exe" package -f -m -S "$RES_DIR" -I "$ANDROID_JAR" -M "$MANIFEST" -J "$GEN_DIR" --auto-add-overlay
if ($LASTEXITCODE -ne 0) { Write-Host "错误: 生成R.java失败"; exit 1 }

Write-Host "[3/8] 编译Java源文件..."
$SOURCE_FILES = Get-ChildItem -Path $SRC_DIR -Filter *.java -Recurse | ForEach-Object { $_.FullName }
$GEN_FILES = Get-ChildItem -Path $GEN_DIR -Filter *.java -Recurse | ForEach-Object { $_.FullName }
$ALL_SOURCES = $SOURCE_FILES + $GEN_FILES
$ALL_SOURCES | Out-File -FilePath "$OUT_DIR\sources.txt" -Encoding UTF8
& "$env:JAVA_HOME\bin\javac.exe" -d "$CLASSES_DIR" -classpath "$ANDROID_JAR" -sourcepath "$SRC_DIR;$GEN_DIR" "@$OUT_DIR\sources.txt"
if ($LASTEXITCODE -ne 0) { Write-Host "错误: 编译Java失败"; exit 1 }

Write-Host "[4/8] 转换为DEX文件..."
# 使用d8（新版Build Tools用d8替代dx）
& "$BUILD_TOOLS\d8.bat" --output "$DEX_FILE" "$CLASSES_DIR"
if ($LASTEXITCODE -ne 0) { 
    # 如果d8失败，尝试dx
    & "$BUILD_TOOLS\dx.bat" --dex --output="$DEX_FILE" "$CLASSES_DIR"
    if ($LASTEXITCODE -ne 0) { Write-Host "错误: 转换DEX失败"; exit 1 }
}

Write-Host "[5/8] 打包APK（未签名）..."
& "$BUILD_TOOLS\aapt.exe" package -f -S "$RES_DIR" -I "$ANDROID_JAR" -M "$MANIFEST" -F "$APK_UNSIGNED" -F "$DEX_FILE"
if ($LASTEXITCODE -ne 0) { Write-Host "错误: 打包APK失败"; exit 1 }

Write-Host "[6/8] 生成签名密钥（如不存在）..."
if (-not (Test-Path $KEYSTORE)) {
    & "$env:JAVA_HOME\bin\keytool.exe" -genkey -v -keystore "$KEYSTORE" -storepass android -alias androiddebugkey -keypass android -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Android Debug,O=Android,C=US"
    if ($LASTEXITCODE -ne 0) { Write-Host "错误: 生成密钥失败"; exit 1 }
}

Write-Host "[7/8] 签名APK..."
& "$BUILD_TOOLS\apksigner.bat" sign --ks "$KEYSTORE" --ks-pass pass:android "$APK_UNSIGNED"
if ($LASTEXITCODE -ne 0) { Write-Host "错误: 签名APK失败"; exit 1 }

Write-Host "[8/8] 重命名APK..."
if (Test-Path $APK_SIGNED) { Remove-Item $APK_SIGNED -Force }
Move-Item "$APK_UNSIGNED" "$APK_SIGNED"

Write-Host ""
Write-Host "========================================"
Write-Host "  构建成功！"
Write-Host "  输出: $APK_SIGNED"
Write-Host "========================================"
Write-Host ""
Write-Host "按任意键退出..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
