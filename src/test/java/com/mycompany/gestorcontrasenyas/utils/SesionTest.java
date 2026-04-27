package com.mycompany.gestorcontrasenyas.utils;

import org.junit.jupiter.api.*;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests para Sesion: ciclo de vida de tokens y masterKey.
 */
@DisplayName("Sesion")
class SesionTest {

    private static final String USER_ID       = "uid-abc-123";
    private static final String ACCESS_TOKEN  = "access.jwt.token";
    private static final String REFRESH_TOKEN = "refresh.jwt.token";
    private static final long   EXPIRA        = 9_999_999_999L;

    private Sesion sesion;

    @BeforeEach
    void setUp() {
        sesion = new Sesion(USER_ID, ACCESS_TOKEN, REFRESH_TOKEN, EXPIRA);
    }

    // ── Getters básicos ───────────────────────────────────────────────

    @Test
    @DisplayName("getUserId devuelve el userId original")
    void getUserId() {
        assertThat(sesion.getUserId()).isEqualTo(USER_ID);
    }

    @Test
    @DisplayName("getAccessToken devuelve el token de acceso")
    void getAccessToken() {
        assertThat(sesion.getAccessToken()).isEqualTo(ACCESS_TOKEN);
    }

    @Test
    @DisplayName("getRefreshToken devuelve el refresh token")
    void getRefreshToken() {
        assertThat(sesion.getRefreshToken()).isEqualTo(REFRESH_TOKEN);
    }

    @Test
    @DisplayName("getAccessTokenExpiraEnEpochSec devuelve la expiración")
    void getExpira() {
        assertThat(sesion.getAccessTokenExpiraEnEpochSec()).isEqualTo(EXPIRA);
    }

    // ── actualizarTokens ──────────────────────────────────────────────

    @Test
    @DisplayName("actualizarTokens reemplaza los tres valores")
    void actualizarTokens_replaces_all_fields() {
        sesion.actualizarTokens("nuevoAccess", "nuevoRefresh", 1234567L);
        assertThat(sesion.getAccessToken()).isEqualTo("nuevoAccess");
        assertThat(sesion.getRefreshToken()).isEqualTo("nuevoRefresh");
        assertThat(sesion.getAccessTokenExpiraEnEpochSec()).isEqualTo(1234567L);
    }

    // ── masterKey ─────────────────────────────────────────────────────

    @Test
    @DisplayName("getMasterKey es null inicialmente")
    void getMasterKey_initially_null() {
        assertThat(sesion.getMasterKey()).isNull();
    }

    @Test
    @DisplayName("setMasterKey y getMasterKey devuelven una copia del array")
    void setAndGetMasterKey_returns_copy() {
        byte[] key = {1, 2, 3, 4};
        sesion.setMasterKey(key);
        byte[] retrieved = sesion.getMasterKey();

        // No es la misma referencia
        assertThat(retrieved).isNotSameAs(key);
        // Pero tiene el mismo contenido
        assertThat(retrieved).isEqualTo(key);
    }

    @Test
    @DisplayName("setMasterKey null limpia la clave")
    void setMasterKey_null_clears_key() {
        sesion.setMasterKey(new byte[]{1, 2, 3});
        sesion.setMasterKey(null);
        assertThat(sesion.getMasterKey()).isNull();
    }

    @Test
    @DisplayName("limpiarMasterKey pone la clave a null")
    void limpiarMasterKey_sets_null() {
        sesion.setMasterKey(new byte[]{10, 20, 30});
        sesion.limpiarMasterKey();
        assertThat(sesion.getMasterKey()).isNull();
    }

    @Test
    @DisplayName("Modificar el array externo no corrompe la masterKey interna")
    void masterKey_is_defensively_copied_on_set() {
        byte[] key = {1, 2, 3, 4};
        sesion.setMasterKey(key);
        key[0] = (byte) 0xFF; // mutamos la referencia externa
        assertThat(sesion.getMasterKey()[0]).isEqualTo((byte) 1); // no afecta
    }

    @Test
    @DisplayName("Modificar el array obtenido por getMasterKey no corrompe la interna")
    void masterKey_is_defensively_copied_on_get() {
        byte[] key = {1, 2, 3, 4};
        sesion.setMasterKey(key);
        byte[] retrieved = sesion.getMasterKey();
        retrieved[0] = (byte) 0xFF;
        assertThat(sesion.getMasterKey()[0]).isEqualTo((byte) 1);
    }

    // ── destruir ──────────────────────────────────────────────────────

    @Test
    @DisplayName("destruir limpia accessToken, refreshToken y masterKey")
    void destruir_clears_all_sensitive_data() {
        sesion.setMasterKey(new byte[]{1, 2, 3});
        sesion.destruir();

        assertThat(sesion.getAccessToken()).isNull();
        assertThat(sesion.getRefreshToken()).isNull();
        assertThat(sesion.getAccessTokenExpiraEnEpochSec()).isEqualTo(0L);
        assertThat(sesion.getMasterKey()).isNull();
    }

    @Test
    @DisplayName("destruir es idempotente (segunda llamada no lanza excepción)")
    void destruir_idempotent() {
        assertThatCode(() -> {
            sesion.destruir();
            sesion.destruir();
        }).doesNotThrowAnyException();
    }
}
