# Script de construcción para GestorContrasenyas
$ErrorActionPreference = "Stop"

# --- Configuración ---
$AppVersion = "2.2"
$JarSource  = "target\GestorContrasenyas-1.0-SNAPSHOT.jar"
$JarDest    = "dist\GestorContrasenyas.jar"
$NsisScript = "installer.nsi"
$MavenPath  = "C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd"
$NsisPath   = "C:\Program Files (x86)\NSIS\makensis.exe"

Write-Host "--- Iniciando construcción GestorContrasenyas v$AppVersion ---" -ForegroundColor Cyan

# 1. Limpieza
Write-Host "1. Limpiando carpetas antiguas..." -ForegroundColor Yellow
foreach ($f in @("dist", "release")) {
    if (Test-Path $f) { Remove-Item -Recurse -Force $f -ErrorAction SilentlyContinue }
}

# 2. Compilar con Maven
Write-Host "2. Compilando con Maven (Clean and Build)..." -ForegroundColor Yellow
& $MavenPath clean package -q
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Maven falló. Revisa los errores de compilación." -ForegroundColor Red
    exit 1
}
Write-Host "   -> Compilación correcta." -ForegroundColor Green

# 3. Verificar JAR
Write-Host "3. Verificando JAR..." -ForegroundColor Yellow
if (-not (Test-Path $JarSource)) {
    Write-Host "ERROR: No se encontró el JAR en '$JarSource'." -ForegroundColor Red
    exit 1
}
Write-Host "   -> JAR encontrado." -ForegroundColor Green

# 4. Preparar dist
Write-Host "4. Preparando carpeta dist..." -ForegroundColor Yellow
New-Item -ItemType Directory -Path dist    -Force | Out-Null
New-Item -ItemType Directory -Path release -Force | Out-Null
Copy-Item $JarSource -Destination $JarDest
Write-Host "   -> JAR copiado a dist/." -ForegroundColor Green

# 5. Compilar wrapper ejecutable con NSIS
Write-Host "5. Compilando wrapper ejecutable con NSIS..." -ForegroundColor Yellow
& $NsisPath "wrapper.nsi"
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: NSIS falló al compilar wrapper." -ForegroundColor Red
    exit 1
}

# 6. Compilar instalador con NSIS
Write-Host "6. Compilando instalador con NSIS..." -ForegroundColor Yellow
& $NsisPath /DAPP_VERSION=$AppVersion "installer.nsi"
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: NSIS falló al compilar instalador." -ForegroundColor Red
    exit 1
}

# 7. Limpieza final de dist
Write-Host "7. Limpiando carpeta dist temporal..." -ForegroundColor Yellow
Remove-Item -Recurse -Force dist -ErrorAction SilentlyContinue

Write-Host "`n¡TODO LISTO!" -ForegroundColor Green
Write-Host "Instalador: release\GestorContrasenyas-$AppVersion-Setup.exe" -ForegroundColor White