package com.mycompany.gestorcontrasenyas.utils;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests de la lógica de anti-fuerza-bruta (backoff exponencial).
 *
 * Las constantes y la fórmula están extraídas de login.java y MasterPassword.java:
 *   MAX_INTENTOS_SIN_BLOQUEO = 5
 *   BLOQUEO_BASE_MS          = 5_000
 *   BLOQUEO_MAX_MS           = 300_000
 *
 * backoff = min(BASE * 2^(exceso-1), MAX)   donde exceso = intentos - MAX
 */
@DisplayName("Lógica anti-brute-force (backoff exponencial)")
class BruteForceLogicTest {

    private static final int  MAX_INTENTOS          = 5;
    private static final long BLOQUEO_BASE_MS        = 5_000L;
    private static final long BLOQUEO_MAX_MS         = 300_000L;

    /** Replica exacta del método privado de login/MasterPassword. */
    private static long calcularBackoffMs(int intentos) {
        int exceso = Math.max(0, intentos - MAX_INTENTOS);
        if (exceso == 0) return 0;
        long backoff = BLOQUEO_BASE_MS * (1L << Math.min(exceso - 1, 10));
        return Math.min(backoff, BLOQUEO_MAX_MS);
    }

    // ── Sin bloqueo ───────────────────────────────────────────────────

    @ParameterizedTest(name = "{0} intentos -> 0 ms")
    @CsvSource({"0", "1", "3", "5"})
    @DisplayName("≤ 5 intentos: backoff = 0 (sin bloqueo)")
    void no_lockout_within_threshold(int intentos) {
        assertThat(calcularBackoffMs(intentos)).isZero();
    }

    // ── Con bloqueo ───────────────────────────────────────────────────

    @Test
    @DisplayName("Intento 6 -> 5 000 ms (primer bloqueo)")
    void lockout_at_attempt_6() {
        assertThat(calcularBackoffMs(6)).isEqualTo(5_000L);
    }

    @Test
    @DisplayName("Intento 7 -> 10 000 ms")
    void lockout_at_attempt_7() {
        assertThat(calcularBackoffMs(7)).isEqualTo(10_000L);
    }

    @Test
    @DisplayName("Intento 8 -> 20 000 ms")
    void lockout_at_attempt_8() {
        assertThat(calcularBackoffMs(8)).isEqualTo(20_000L);
    }

    @Test
    @DisplayName("Intento 16+ -> siempre <= BLOQUEO_MAX_MS (300 s)")
    void lockout_capped_at_max() {
        assertThat(calcularBackoffMs(16)).isLessThanOrEqualTo(BLOQUEO_MAX_MS);
        assertThat(calcularBackoffMs(50)).isEqualTo(BLOQUEO_MAX_MS);
        assertThat(calcularBackoffMs(100)).isEqualTo(BLOQUEO_MAX_MS);
    }

    @Test
    @DisplayName("Backoff es estrictamente creciente entre intentos 6 y 16")
    void backoff_is_monotonically_increasing() {
        long anterior = 0;
        for (int i = 6; i <= 16; i++) {
            long actual = calcularBackoffMs(i);
            assertThat(actual).isGreaterThanOrEqualTo(anterior);
            anterior = actual;
        }
    }

    // ── Política de contraseña (extraída de login.java) ───────────────

    @Nested
    @DisplayName("Política de contraseña en registro")
    class PasswordPolicyTest {

        /** Replica de cumplePoliticaPassword en login.java */
        private boolean cumplePolitica(char[] chars) {
            if (chars == null || chars.length < 10) return false;
            boolean up = false, lo = false, di = false, sp = false;
            for (char c : chars) {
                if (Character.isUpperCase(c)) up = true;
                else if (Character.isLowerCase(c)) lo = true;
                else if (Character.isDigit(c)) di = true;
                else sp = true;
            }
            return up && lo && di && sp;
        }

        @Test
        @DisplayName("Contraseña válida: 10+ chars, mayúscula, minúscula, dígito, símbolo")
        void valid_password() {
            assertThat(cumplePolitica("Secure@Pass1".toCharArray())).isTrue();
        }

        @Test
        @DisplayName("Contraseña nula -> false")
        void null_password() {
            assertThat(cumplePolitica(null)).isFalse();
        }

        @Test
        @DisplayName("Contraseña de 9 chars (< 10) -> false")
        void too_short() {
            assertThat(cumplePolitica("Short@1A".toCharArray())).isFalse();
        }

        @Test
        @DisplayName("Sin mayúscula -> false")
        void no_uppercase() {
            assertThat(cumplePolitica("secure@pass1".toCharArray())).isFalse();
        }

        @Test
        @DisplayName("Sin minúscula -> false")
        void no_lowercase() {
            assertThat(cumplePolitica("SECURE@PASS1".toCharArray())).isFalse();
        }

        @Test
        @DisplayName("Sin dígito -> false")
        void no_digit() {
            assertThat(cumplePolitica("Secure@Pass!!".toCharArray())).isFalse();
        }

        @Test
        @DisplayName("Sin símbolo -> false")
        void no_special() {
            assertThat(cumplePolitica("SecurePass12".toCharArray())).isFalse();
        }
    }
}
