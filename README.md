# 🔐 GestorContraseñas

Aplicación de escritorio para gestionar de forma segura las credenciales de tus cuentas de juegos y servicios online. Los datos se almacenan encriptados con AES-256 en la nube mediante Supabase, y solo tú puedes acceder a ellos gracias a una contraseña maestra personal.

## Características

- Almacenamiento seguro de credenciales con encriptación AES-256
- Contraseña maestra personal derivada con PBKDF2 — nunca se guarda en ningún sitio
- Registro de cuentas por categoría (Gmail, Riot account, Albion, Otros)
- Visualización del rango actual de Valorant y League of Legends para cuentas de Riot
- Autenticación de usuarios con Supabase Auth
- Base de datos en la nube con Row Level Security — cada usuario solo ve sus propios datos
- Recuperación de contraseña por email

## 🚀 Descarga e Instalación

La forma más sencilla de usar TrackLectura en Windows es descargando el instalador oficial desde nuestra sección de lanzamientos:

👉 **[Descargar Gestror-Contraseñas v1.3(.exe)](https://github.com/Pau-Balsach/gestor-contrasenas-code/releases/latest/download/GestorContrasenyas-1.3-Setup.exe)**

## Requisitos

- Java 25 o superior

## Tecnologías utilizadas

- Java 25
- Maven
- Supabase (Auth + PostgreSQL + REST API)
- AES-256 + PBKDF2 para encriptación
- Riot Games API (League of Legends)
- Henrik Dev API (Valorant)

## Seguridad

- Las contraseñas se cifran en cliente con **AES-GCM (v2)**, incluyendo integridad autenticada.
- Compatibilidad con datos legacy **AES-CBC (v1 / sin prefijo)** para lectura durante migración.
- La clave de cifrado se deriva de tu contraseña maestra con PBKDF2 y nunca se almacena.
- Row Level Security activado en Supabase — ningún usuario puede acceder a los datos de otro.
- La contraseña maestra es independiente de la contraseña de login.
- Protección anti brute-force en cliente (backoff y bloqueo temporal).

## Licencia

MIT License — ver archivo [LICENSE](LICENSE)
