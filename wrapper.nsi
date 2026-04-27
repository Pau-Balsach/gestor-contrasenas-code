!include "FileFunc.nsh"

!define APP_NAME        "GestorContrasenyas"
!define JAR_FILE        "GestorContrasenyas.jar"

Icon "assets\icon.ico"
OutFile "dist\${APP_NAME}.exe"
RequestExecutionLevel user
SilentInstall silent

Section "Main"
    InitPluginsDir
    SetOutPath "$PLUGINSDIR"
    File "dist\${JAR_FILE}"
    
    SetOutPath "$EXEDIR"
    ${GetParameters} $R0
    ExecWait '"javaw.exe" -jar "$PLUGINSDIR\${JAR_FILE}" $R0'
SectionEnd
