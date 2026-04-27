package com.mycompany.gestorcontrasenyas.model;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests para los modelos Categoria y Cuenta.
 */
@DisplayName("Modelos")
class ModeloTest {

    // ─────────────────────────────────────────────────────────────────
    // Categoria
    // ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Categoria")
    class CategoriaTests {

        @Test
        @DisplayName("Constructor y getters funcionan correctamente")
        void constructor_and_getters() {
            Categoria cat = new Categoria(42L, "Gaming", false);
            assertThat(cat.getId()).isEqualTo(42L);
            assertThat(cat.getNombre()).isEqualTo("Gaming");
            assertThat(cat.isEsRiot()).isFalse();
        }

        @Test
        @DisplayName("isEsRiot true cuando es categoría Riot")
        void isEsRiot_true() {
            Categoria cat = new Categoria(1L, "Riot account", true);
            assertThat(cat.isEsRiot()).isTrue();
        }

        @Test
        @DisplayName("toString devuelve el nombre")
        void toString_returns_nombre() {
            Categoria cat = new Categoria(5L, "Social", false);
            assertThat(cat.toString()).isEqualTo("Social");
        }

        @Test
        @DisplayName("Nombre con caracteres especiales y Unicode")
        void nombre_unicode() {
            Categoria cat = new Categoria(1L, "Correo 📧", false);
            assertThat(cat.getNombre()).isEqualTo("Correo 📧");
        }

        @Test
        @DisplayName("Id puede ser 0 (valor límite)")
        void id_zero() {
            Categoria cat = new Categoria(0L, "Misc", false);
            assertThat(cat.getId()).isZero();
        }

        @Test
        @DisplayName("Id puede ser Long.MAX_VALUE (valor extremo)")
        void id_max_long() {
            Categoria cat = new Categoria(Long.MAX_VALUE, "Extremo", false);
            assertThat(cat.getId()).isEqualTo(Long.MAX_VALUE);
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // Cuenta
    // ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Cuenta")
    class CuentaTests {

        @Test
        @DisplayName("Constructor y getters básicos")
        void constructor_and_getters() {
            Cuenta cuenta = new Cuenta("Gaming", "user123", "pass456", null);
            assertThat(cuenta.getCategoria()).isEqualTo("Gaming");
            assertThat(cuenta.getUsuario()).isEqualTo("user123");
            assertThat(cuenta.getPassword()).isEqualTo("pass456");
            assertThat(cuenta.getRiotId()).isNull();
        }

        @Test
        @DisplayName("riotId se almacena cuando se proporciona")
        void riotId_stored() {
            Cuenta cuenta = new Cuenta("Riot account", "jugador#EUW", "********", "jugador#EUW");
            assertThat(cuenta.getRiotId()).isEqualTo("jugador#EUW");
        }

        @Test
        @DisplayName("La contraseña puede ser el placeholder de asteriscos")
        void password_placeholder() {
            Cuenta cuenta = new Cuenta("Banco", "usuario", "********", null);
            assertThat(cuenta.getPassword()).isEqualTo("********");
        }

        @Test
        @DisplayName("Cuenta con todos los campos nulos no lanza NPE")
        void all_nulls_no_exception() {
            assertThatCode(() -> new Cuenta(null, null, null, null)).doesNotThrowAnyException();
        }
    }
}
