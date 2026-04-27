package com.mycompany.gestorcontrasenyas.service;

import com.mycompany.gestorcontrasenyas.utils.Encriptacion;
import com.mycompany.gestorcontrasenyas.utils.Pbkdf2;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.google.gson.JsonObject;
import com.mycompany.gestorcontrasenyas.db.SupabaseAuth;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests de AuthService usando MockedStatic de SupabaseAuth para aislar
 * la capa de red.
 *
 * Todos los tests están desconectados de Internet.
 */
@DisplayName("AuthService")
class AuthServiceTest {

    private static final String TEXTO_VERIFICACION = "gestor-contrasenyas-ok";

    // ── login delegado a SupabaseAuth ─────────────────────────────────

    @Test
    @DisplayName("login: delega en SupabaseAuth.login y retorna null en éxito")
    void login_delegates_to_supabase_and_returns_null_on_success() {
        try (MockedStatic<SupabaseAuth> mocked = mockStatic(SupabaseAuth.class)) {
            mocked.when(() -> SupabaseAuth.login(anyString(), any(char[].class)))
                  .thenReturn(null);

            String result = AuthService.login("user@test.com", "pass".toCharArray());
            assertThat(result).isNull();
        }
    }

    @Test
    @DisplayName("login: propaga el mensaje de error de SupabaseAuth")
    void login_propagates_error_message() {
        try (MockedStatic<SupabaseAuth> mocked = mockStatic(SupabaseAuth.class)) {
            mocked.when(() -> SupabaseAuth.login(anyString(), any(char[].class)))
                  .thenReturn("Email o contraseña incorrectos.");

            String result = AuthService.login("bad@test.com", "wrong".toCharArray());
            assertThat(result).isEqualTo("Email o contraseña incorrectos.");
        }
    }

    // ── tieneMasterKey ────────────────────────────────────────────────

    @Test
    @DisplayName("tieneMasterKey: true cuando salt != null y sesión vigente")
    void tieneMasterKey_true_when_salt_exists() {
        try (MockedStatic<SupabaseAuth> mocked = mockStatic(SupabaseAuth.class)) {
            mocked.when(SupabaseAuth::asegurarTokenVigente).thenReturn(null);
            mocked.when(() -> SupabaseAuth.obtenerSalt(anyString())).thenReturn("saltBase64");

            assertThat(AuthService.tieneMasterKey("uid-123")).isTrue();
        }
    }

    @Test
    @DisplayName("tieneMasterKey: false cuando salt es null")
    void tieneMasterKey_false_when_no_salt() {
        try (MockedStatic<SupabaseAuth> mocked = mockStatic(SupabaseAuth.class)) {
            mocked.when(SupabaseAuth::asegurarTokenVigente).thenReturn(null);
            mocked.when(() -> SupabaseAuth.obtenerSalt(anyString())).thenReturn(null);

            assertThat(AuthService.tieneMasterKey("uid-123")).isFalse();
        }
    }

    @Test
    @DisplayName("tieneMasterKey: false cuando la sesión no es válida")
    void tieneMasterKey_false_when_session_invalid() {
        try (MockedStatic<SupabaseAuth> mocked = mockStatic(SupabaseAuth.class)) {
            mocked.when(SupabaseAuth::asegurarTokenVigente).thenReturn("No hay sesión activa.");

            assertThat(AuthService.tieneMasterKey("uid-123")).isFalse();
        }
    }

    // ── configurarMasterKey: primer registro ──────────────────────────

    @Test
    @DisplayName("configurarMasterKey: primer registro — guarda salt+verificador y devuelve null")
    void configurarMasterKey_first_registration_ok() {
        try (MockedStatic<SupabaseAuth> mocked = mockStatic(SupabaseAuth.class)) {
            mocked.when(SupabaseAuth::asegurarTokenVigente).thenReturn(null);
            mocked.when(SupabaseAuth::getUserId).thenReturn("uid-nuevo");
            mocked.when(() -> SupabaseAuth.obtenerDatosUsuario("uid-nuevo")).thenReturn(null);
            mocked.when(() -> SupabaseAuth.guardarUsuario(anyString(), anyString(), anyString()))
                  .thenReturn(null);

            String error = AuthService.configurarMasterKey("Master@Password1".toCharArray());
            assertThat(error).isNull();

            // Verifica que se llamó a guardarUsuario con los parámetros correctos
            mocked.verify(() -> SupabaseAuth.guardarUsuario(eq("uid-nuevo"), anyString(), anyString()));
            // Y que se guardó la masterKey en sesión
            mocked.verify(() -> SupabaseAuth.setMasterKey(any(byte[].class)));
        }
    }

    @Test
    @DisplayName("configurarMasterKey: error de red al guardar -> propaga el mensaje")
    void configurarMasterKey_network_error_propagated() {
        try (MockedStatic<SupabaseAuth> mocked = mockStatic(SupabaseAuth.class)) {
            mocked.when(SupabaseAuth::asegurarTokenVigente).thenReturn(null);
            mocked.when(SupabaseAuth::getUserId).thenReturn("uid-new");
            mocked.when(() -> SupabaseAuth.obtenerDatosUsuario("uid-new")).thenReturn(null);
            mocked.when(() -> SupabaseAuth.guardarUsuario(anyString(), anyString(), anyString()))
                  .thenReturn("Error al guardar usuario.");

            String error = AuthService.configurarMasterKey("Master@Password1".toCharArray());
            assertThat(error).isEqualTo("Error al guardar usuario.");
        }
    }

    // ── configurarMasterKey: usuario existente (verificación) ─────────

    @Test
    @DisplayName("configurarMasterKey: contraseña maestra correcta para usuario existente -> null")
    void configurarMasterKey_existing_user_correct_password() throws Exception {
        // Construimos datos reales de verificador para que el test sea fiable
        String salt         = Pbkdf2.generarSalt();
        byte[] key          = Pbkdf2.derivarKey("MiMaster@2024!".toCharArray(), salt);
        String verificador  = Encriptacion.encrypt(key, TEXTO_VERIFICACION);

        JsonObject datos = new JsonObject();
        datos.addProperty("salt", salt);
        datos.addProperty("verificador", verificador);

        try (MockedStatic<SupabaseAuth> mocked = mockStatic(SupabaseAuth.class)) {
            mocked.when(SupabaseAuth::asegurarTokenVigente).thenReturn(null);
            mocked.when(SupabaseAuth::getUserId).thenReturn("uid-existente");
            mocked.when(() -> SupabaseAuth.obtenerDatosUsuario("uid-existente")).thenReturn(datos);

            String error = AuthService.configurarMasterKey("MiMaster@2024!".toCharArray());
            assertThat(error).isNull();

            // Se debe guardar la masterKey derivada
            mocked.verify(() -> SupabaseAuth.setMasterKey(any(byte[].class)));
        }
    }

    @Test
    @DisplayName("configurarMasterKey: contraseña maestra incorrecta -> mensaje de error")
    void configurarMasterKey_existing_user_wrong_password() throws Exception {
        String salt         = Pbkdf2.generarSalt();
        byte[] key          = Pbkdf2.derivarKey("Correcta@2024!".toCharArray(), salt);
        String verificador  = Encriptacion.encrypt(key, TEXTO_VERIFICACION);

        JsonObject datos = new JsonObject();
        datos.addProperty("salt", salt);
        datos.addProperty("verificador", verificador);

        try (MockedStatic<SupabaseAuth> mocked = mockStatic(SupabaseAuth.class)) {
            mocked.when(SupabaseAuth::asegurarTokenVigente).thenReturn(null);
            mocked.when(SupabaseAuth::getUserId).thenReturn("uid-existente");
            mocked.when(() -> SupabaseAuth.obtenerDatosUsuario("uid-existente")).thenReturn(datos);

            String error = AuthService.configurarMasterKey("Incorrecta@2024!".toCharArray());
            assertThat(error).isEqualTo("Contraseña maestra incorrecta.");
        }
    }

    @Test
    @DisplayName("configurarMasterKey: sin sesión activa -> retorna mensaje de error")
    void configurarMasterKey_no_active_session() {
        try (MockedStatic<SupabaseAuth> mocked = mockStatic(SupabaseAuth.class)) {
            mocked.when(SupabaseAuth::asegurarTokenVigente).thenReturn("No hay sesión activa.");

            String error = AuthService.configurarMasterKey("cualquier".toCharArray());
            assertThat(error).isEqualTo("No hay sesión activa.");
        }
    }

    // ── signup ────────────────────────────────────────────────────────

    @Test
    @DisplayName("signup: delega en SupabaseAuth y retorna null en éxito")
    void signup_delegates_and_returns_null() {
        try (MockedStatic<SupabaseAuth> mocked = mockStatic(SupabaseAuth.class)) {
            mocked.when(() -> SupabaseAuth.signup(anyString(), any(char[].class)))
                  .thenReturn(null);

            String result = AuthService.signup("new@user.com", "Password1@".toCharArray());
            assertThat(result).isNull();
        }
    }

    @Test
    @DisplayName("signup: email ya registrado -> propaga mensaje de error")
    void signup_already_registered_email() {
        try (MockedStatic<SupabaseAuth> mocked = mockStatic(SupabaseAuth.class)) {
            mocked.when(() -> SupabaseAuth.signup(anyString(), any(char[].class)))
                  .thenReturn("El email ya está en uso.");

            String result = AuthService.signup("existing@user.com", "Password1@".toCharArray());
            assertThat(result).isEqualTo("El email ya está en uso.");
        }
    }
}
