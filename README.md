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

Próximamente disponible en la sección de lanzamientos.

## Requisitos

- Java 25 o superior
- Maven

## 🛠️ Compilar y ejecutar

Antes de compilar necesitas crear los archivos de configuración con tus propias credenciales:

- `src/com/mycompany/gestorcontrasenyas/db/Conexion.java` — credenciales de Supabase PostgreSQL
- `src/com/mycompany/gestorcontrasenyas/db/SupabaseAuth.java` — URL y ANON KEY de Supabase
- `src/com/mycompany/gestorcontrasenyas/service/RiotService.java` — API keys de Riot Games y Henrik

**Windows (PowerShell)**
```powershell
mvn compile
mvn exec:java
```

**Linux / macOS**
```bash
mvn compile
mvn exec:java
```

## Tecnologías utilizadas

- Java 25
- Maven
- Supabase (Auth + PostgreSQL + REST API)
- AES-256 + PBKDF2 para encriptación
- Riot Games API (League of Legends)
- Henrik Dev API (Valorant)

## Seguridad

- Las contraseñas se encriptan con AES-256 antes de guardarse en la base de datos
- La clave de encriptación se deriva de tu contraseña maestra con PBKDF2 y nunca se almacena
- Row Level Security activado en Supabase — ningún usuario puede acceder a los datos de otro
- La contraseña maestra es independiente de la contraseña de login

## Licencia

MIT License — ver archivo [LICENSE](LICENSE)
