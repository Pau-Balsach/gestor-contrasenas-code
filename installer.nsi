; Script NSIS para GestorContrasenyas (Instalador)
!define APP_NAME        "GestorContrasenyas"
!ifndef APP_VERSION
    !define APP_VERSION "2.2"
!endif
!define APP_PUBLISHER   "pbalsach"
!define EXE_FILE        "GestorContrasenyas.exe"
!define INSTALL_DIR     "$PROGRAMFILES64\${APP_NAME}"

Icon                    "assets\icon.ico"
OutFile "release\GestorContrasenyas-${APP_VERSION}-Setup.exe"
InstallDir "${INSTALL_DIR}"
RequestExecutionLevel admin
Name "${APP_NAME} ${APP_VERSION}"

Page directory
Page instfiles

Section "Instalar ${APP_NAME}" SecMain
    SetOutPath "$INSTDIR"

    ; Copiar el ejecutable
    File "dist\${EXE_FILE}"

    ; Copiar el icono
    File "assets\icon.ico"

    ; Crear config.properties
    FileOpen $0 "$INSTDIR\config.properties" w
    FileWrite $0 "# Configuracion de la aplicacion$\r$\n"
    FileWrite $0 "# IMPORTANTE: No subas este fichero a GitHub$\r$\n"
    FileWrite $0 "supabase.url=https://aodcjvqhnjuuccnhrxqg.supabase.co$\r$\n"
    FileWrite $0 "supabase.anon_key=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFvZGNqdnFobmp1dWNjbmhyeHFnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQxNDk5NTgsImV4cCI6MjA4OTcyNTk1OH0.lW2jXVyVlk8pkKkFdCZ6z98Sw4Af4zWVC2wjLDEOXIo$\r$\n"
    FileWrite $0 "riot.api_key=RGAPI-57da7356-4c42-42ec-b4f8-38fdaa71c4a6$\r$\n"
    FileWrite $0 "henrik.api_key=HDEV-5cb94d57-25ad-49a9-a902-04c4f3ef6d92$\r$\n"
    FileClose $0

    ; Acceso directo en escritorio con icono
    CreateShortcut "$DESKTOP\${APP_NAME}.lnk" "$INSTDIR\${EXE_FILE}" "" "$INSTDIR\icon.ico"

    ; Acceso directo en menú inicio con icono
    CreateDirectory "$SMPROGRAMS\${APP_NAME}"
    CreateShortcut "$SMPROGRAMS\${APP_NAME}\${APP_NAME}.lnk" "$INSTDIR\${EXE_FILE}" "" "$INSTDIR\icon.ico"
    CreateShortcut "$SMPROGRAMS\${APP_NAME}\Desinstalar.lnk" "$INSTDIR\Uninstall.exe"

    ; Registrar en Windows
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}" "DisplayName" "${APP_NAME}"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}" "UninstallString" "$INSTDIR\Uninstall.exe"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}" "DisplayVersion" "${APP_VERSION}"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}" "Publisher" "${APP_PUBLISHER}"
    WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}" "DisplayIcon" "$INSTDIR\icon.ico"

    WriteUninstaller "$INSTDIR\Uninstall.exe"
SectionEnd

Section "Uninstall"
    Delete "$INSTDIR\${EXE_FILE}"
    Delete "$INSTDIR\config.properties"
    Delete "$INSTDIR\icon.ico"
    Delete "$INSTDIR\Uninstall.exe"
    RMDir "$INSTDIR"

    Delete "$DESKTOP\${APP_NAME}.lnk"
    Delete "$SMPROGRAMS\${APP_NAME}\${APP_NAME}.lnk"
    Delete "$SMPROGRAMS\${APP_NAME}\Desinstalar.lnk"
    RMDir "$SMPROGRAMS\${APP_NAME}"

    DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${APP_NAME}"
SectionEnd