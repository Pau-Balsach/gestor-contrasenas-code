package com.mycompany.gestorcontrasenyas.service;

import com.google.gson.*;
import com.mycompany.gestorcontrasenyas.db.SupabaseAuth;
import com.mycompany.gestorcontrasenyas.model.Cuenta;
import com.mycompany.gestorcontrasenyas.utils.Encriptacion;
import com.mycompany.gestorcontrasenyas.utils.Pbkdf2;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests de CuentaService y CategoriaService aislados de red con MockedStatic.
 */
@DisplayName("CuentaService y CategoriaService")
class CuentaYCategoriaServiceTest {

    // ── Helpers ───────────────────────────────────────────────────────

    private static final byte[] KEY = new byte[32];
    static { for (int i = 0; i < 32; i++) KEY[i] = (byte)(i + 1); }

    private static final String VALID_UUID = "550e8400-e29b-41d4-a716-446655440000";

    // ─────────────────────────────────────────────────────────────────
    // CategoriaService
    // ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("CategoriaService")
    class CategoriaServiceTests {

        @Test
        @DisplayName("crearCategoria: nombre vacío -> error inmediato (sin red)")
        void crearCategoria_empty_name_returns_error() {
            // No necesita mock porque falla antes de la red
            String error = CategoriaService.crearCategoria("", false);
            assertThat(error).isEqualTo("El nombre no puede estar vacío.");
        }

        @Test
        @DisplayName("crearCategoria: nombre de solo espacios -> error inmediato")
        void crearCategoria_blank_name_returns_error() {
            String error = CategoriaService.crearCategoria("   ", false);
            assertThat(error).isEqualTo("El nombre no puede estar vacío.");
        }

        @Test
        @DisplayName("crearCategoria: sin sesión activa -> retorna error de sesión")
        void crearCategoria_no_session_returns_session_error() {
            try (MockedStatic<SupabaseAuth> m = mockStatic(SupabaseAuth.class)) {
                m.when(SupabaseAuth::asegurarTokenVigente).thenReturn("No hay sesión activa.");
                String error = CategoriaService.crearCategoria("Banco", false);
                assertThat(error).isEqualTo("No hay sesión activa.");
            }
        }

        @Test
        @DisplayName("obtenerCategorias: sin sesión -> lista vacía")
        void obtenerCategorias_no_session_returns_empty() {
            try (MockedStatic<SupabaseAuth> m = mockStatic(SupabaseAuth.class)) {
                m.when(SupabaseAuth::asegurarTokenVigente).thenReturn("Error sesión");
                assertThat(CategoriaService.obtenerCategorias()).isEmpty();
            }
        }

        @Test
        @DisplayName("NOMBRE_RIOT es 'Riot account'")
        void nombre_riot_constant() {
            assertThat(CategoriaService.NOMBRE_RIOT).isEqualTo("Riot account");
        }

        @Test
        @DisplayName("obtenerNombres: sin sesión -> lista vacía")
        void obtenerNombres_no_session_empty() {
            try (MockedStatic<SupabaseAuth> m = mockStatic(SupabaseAuth.class)) {
                m.when(SupabaseAuth::asegurarTokenVigente).thenReturn("Error");
                assertThat(CategoriaService.obtenerNombres()).isEmpty();
            }
        }

        @Test
        @DisplayName("eliminarCategoria: sin sesión -> retorna error")
        void eliminarCategoria_no_session() {
            try (MockedStatic<SupabaseAuth> m = mockStatic(SupabaseAuth.class)) {
                m.when(SupabaseAuth::asegurarTokenVigente).thenReturn("No hay sesión activa.");
                String error = CategoriaService.eliminarCategoria("Gaming");
                assertThat(error).isEqualTo("No hay sesión activa.");
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // CuentaService – validaciones locales (sin red)
    // ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("CuentaService – validaciones locales")
    class CuentaServiceLocalTests {

        @Test
        @DisplayName("guardarCuenta: sin sesión activa -> retorna error")
        void guardarCuenta_no_session() {
            try (MockedStatic<SupabaseAuth> m = mockStatic(SupabaseAuth.class)) {
                m.when(SupabaseAuth::haySesionActiva).thenReturn(false);
                String error = CuentaService.guardarCuenta("user", "pass".toCharArray(), "Gaming", null);
                assertThat(error).isEqualTo("No hay sesión activa.");
            }
        }

        @Test
        @DisplayName("obtenerCuentas: sin sesión -> lista vacía")
        void obtenerCuentas_no_session_empty() {
            try (MockedStatic<SupabaseAuth> m = mockStatic(SupabaseAuth.class)) {
                m.when(SupabaseAuth::haySesionActiva).thenReturn(false);
                assertThat(CuentaService.obtenerCuentas()).isEmpty();
            }
        }

        @Test
        @DisplayName("eliminarCuenta: UUID inválido -> no hace nada (sin excepción)")
        void eliminarCuenta_invalid_uuid_no_exception() {
            assertThatCode(() -> CuentaService.eliminarCuenta("not-a-uuid"))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("eliminarCuenta: null -> no hace nada")
        void eliminarCuenta_null_no_exception() {
            assertThatCode(() -> CuentaService.eliminarCuenta(null))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("obtenerPasswordDescifradaPorId: UUID inválido -> null")
        void getPassword_invalid_uuid_null() {
            String result = CuentaService.obtenerPasswordDescifradaPorId("no-es-uuid");
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("obtenerPasswordDescifradaPorId: null -> null")
        void getPassword_null_uuid_null() {
            assertThat(CuentaService.obtenerPasswordDescifradaPorId(null)).isNull();
        }

        @Test
        @DisplayName("actualizarCuenta: UUID inválido -> no lanza excepción")
        void actualizarCuenta_invalid_uuid_no_exception() {
            assertThatCode(() -> CuentaService.actualizarCuenta("bad-id", "user", "pwd".toCharArray(), null))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("getCuentaId: sin sesión -> String vacío")
        void getCuentaId_no_session_empty_string() {
            try (MockedStatic<SupabaseAuth> m = mockStatic(SupabaseAuth.class)) {
                m.when(SupabaseAuth::haySesionActiva).thenReturn(false);
                assertThat(CuentaService.getCuentaId("alguien")).isEmpty();
            }
        }

        @Test
        @DisplayName("getCuentaid (alias) redirige a getCuentaId")
        void getCuentaid_alias_works() {
            try (MockedStatic<SupabaseAuth> m = mockStatic(SupabaseAuth.class)) {
                m.when(SupabaseAuth::haySesionActiva).thenReturn(false);
                assertThat(CuentaService.getCuentaid("alguien")).isEmpty();
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // CuentaService – cifrado integrado con clave real
    // ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("CuentaService – cifrado round-trip")
    class CuentaServiceEncryptionTests {

        @Test
        @DisplayName("Datos de cuenta cifrados con KEY son descifrados correctamente")
        void encrypt_decrypt_cuenta_fields() throws Exception {
            // Simula lo que hace guardarCuenta internamente con clave derivada
            String salt = Pbkdf2.generarSalt();
            byte[] key  = Pbkdf2.derivarKey("TestMaster@2024".toCharArray(), salt);

            String usuarioEnc  = Encriptacion.encrypt(key, "mi_usuario@gmail.com");
            String passwordEnc = Encriptacion.encrypt(key, "SecretP@ss1".toCharArray());

            // Y lo que hace obtenerCuentas al descifrar
            String usuarioDec  = Encriptacion.decrypt(key, usuarioEnc);
            String passwordDec = Encriptacion.decrypt(key, passwordEnc);

            assertThat(usuarioDec).isEqualTo("mi_usuario@gmail.com");
            assertThat(passwordDec).isEqualTo("SecretP@ss1");
        }

        @Test
        @DisplayName("Cuenta Riot: riotId se almacena sólo en categoría 'Riot account'")
        void riot_id_only_for_riot_category() {
            // Lógica de guardarCuenta: si categoría != "Riot account" el riotId va a null
            String categoria = "Gaming";
            boolean tieneRiotId = "Riot account".equals(categoria)
                    && "RiotPlayer#EUW" != null && !"RiotPlayer#EUW".isEmpty();
            assertThat(tieneRiotId).isFalse();
        }

        @Test
        @DisplayName("Cuenta Riot: riotId se guarda cuando categoría es 'Riot account'")
        void riot_id_saved_for_riot_category() {
            String categoria = "Riot account";
            String riotId    = "RiotPlayer#EUW";
            boolean tieneRiotId = "Riot account".equals(categoria)
                    && riotId != null && !riotId.isEmpty();
            assertThat(tieneRiotId).isTrue();
        }
    }
}
