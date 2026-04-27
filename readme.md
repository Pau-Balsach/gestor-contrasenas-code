# 🔐 GestorContraseñas

> Aplicación de escritorio para gestionar de forma segura las credenciales de tus cuentas de juegos y servicios online.

Los datos se almacenan **encriptados con AES-256** en la nube mediante Supabase, y solo tú puedes acceder a ellos gracias a una **contraseña maestra personal**.

---

## ✨ Características

| | |
|---|---|
| 🔒 | Encriptación AES-256-GCM en cliente, con integridad autenticada |
| 🧠 | Contraseña maestra derivada con PBKDF2 — nunca se almacena |
| 📁 | Organización por categorías (Gmail, Riot account, Albion, Otros…) |
| 🎮 | Rango actual de **Valorant** y **League of Legends** para cuentas Riot |
| ☁️ | Autenticación y base de datos en la nube con **Supabase Auth** |
| 🛡️ | Row Level Security — cada usuario solo ve sus propios datos |
| 📧 | Recuperación de contraseña por email |
| 🚫 | Protección anti brute-force con backoff exponencial y bloqueo temporal |

---

## 🚀 Descarga e instalación

La forma más sencilla de usar GestorContraseñas en Windows es descargando el instalador oficial:

👉 **[Descargar GestorContraseñas v2.2 (.exe)](https://github.com/Pau-Balsach/gestor-contrasenas-code/releases/latest/download/GestorContrasenyas-2.2-Setup.exe)**

> ⚠️ Requiere **Java 25** o superior instalado en el sistema.

---

## 🛠️ Tecnologías utilizadas

- **Java 25** + Maven
- **Supabase** — Auth, PostgreSQL y REST API
- **AES-256-GCM** + **PBKDF2** para cifrado y derivación de clave
- **Riot Games API** — rangos de League of Legends
- **Henrik Dev API** — rangos de Valorant

---

## 🔑 Seguridad en detalle

- Las contraseñas se cifran en cliente con **AES-GCM (v2)**, que incluye integridad autenticada.
- La clave de cifrado se deriva de tu contraseña maestra con **PBKDF2** y **nunca se almacena** en ningún sitio.
- Compatibilidad con datos legacy **AES-CBC (v1)** para lectura durante la migración, con migración transparente a `v2` al leer.
- **Row Level Security** activado en Supabase — ningún usuario puede acceder a los datos de otro.
- La contraseña maestra es **completamente independiente** de la contraseña de login.
- Protección **anti brute-force** con backoff exponencial y bloqueo temporal, tanto en login como en acceso a la master key.
- La configuración admite **variables de entorno** como alternativa a `config.properties`.

---

## 📄 Licencia

MIT License — ver archivo [LICENSE](LICENSE)