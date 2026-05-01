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

👉 **[Descargar Gestror-Contraseñas v2.0(.exe)](https://github.com/Pau-Balsach/gestor-contrasenas-code/releases/latest/download/GestorContrasenyas-2.0-Setup.exe)**

## Requisitos

- Java 25 o superior

## Tecnologías utilizadas

- Java 25
- Maven
- Supabase (Auth + PostgreSQL + REST API)
- AES-256-GCM (v2) + AES-256-CBC (v1 legacy) para encriptación
- PBKDF2WithHmacSHA256 (310 000 iteraciones) para derivación de clave maestra
- Riot Games API (League of Legends — ranked entries by PUUID)
- Henrik Dev API (Valorant MMR v3)
- Gson para serialización/deserialización JSON
- JUnit 5 + AssertJ + Mockito (MockedStatic) para tests

## 🧪 Tests

Los tests se ubican en `src/test/` y cubren modelos, servicios y utilidades sin conexión a Internet real (`MockedStatic` de Mockito aísla las llamadas a Supabase).

| Clase de test | Qué se verifica |
| :--- | :--- |
| **ModeloTest** | Constructores, getters y casos límite de `Categoria` y `Cuenta` |
| **AuthServiceTest** | `login`, `signup`, `tieneMasterKey` y `configurarMasterKey` (primer registro, contraseña correcta/incorrecta, sin sesión) |
| **CuentaYCategoriaServiceTest** | Validaciones locales de `CategoriaService` y `CuentaService` (sin sesión, UUID inválido, round-trip de cifrado, lógica de `riotId`) |
| **RiotServiceTest** | Parsing de `riotId` y comportamiento con puuid/riotId null, vacío o mal formado (sin red) |
| **BruteForceLogicTest** | Backoff exponencial (valores exactos, techo en 300 s) y política de contraseña en registro |
| **EncriptacionTest** | Round-trip AES-GCM, IV aleatorio, compatibilidad AES-CBC legacy (v1/sin prefijo), entradas inválidas → null |
| **Pbkdf2Test** | Salt aleatorio, derivación determinista de 32 bytes, sensibilidad a password y salt, integración con `Encriptacion` |
| **SesionTest** | Ciclo de vida de tokens, copia defensiva de `masterKey` y limpieza segura con `destruir` |

Para ejecutar los tests:

```bash
mvn test
```

## Seguridad

- Las contraseñas se cifran en cliente con **AES-GCM (v2)**, incluyendo integridad autenticada.
- Compatibilidad con datos legacy **AES-CBC (v1 / sin prefijo)** para lectura durante migración.
- La clave de cifrado se deriva de tu contraseña maestra con PBKDF2 y nunca se almacena.
- Row Level Security activado en Supabase — ningún usuario puede acceder a los datos de otro.
- La contraseña maestra es independiente de la contraseña de login.
- Protección anti brute-force en cliente (backoff y bloqueo temporal), tanto en login como en acceso de master key.
- Migración transparente de cifrados legacy a `v2` al leer cuentas.
- La configuración admite variables de entorno para evitar depender solo de `config.properties`.

## Licencia

MIT License — ver archivo [LICENSE](LICENSE)