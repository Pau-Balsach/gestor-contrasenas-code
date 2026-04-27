package com.mycompany.gestorcontrasenyas.utils;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Base64;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests para Pbkdf2 (derivación de clave y generación de salt).
 */
@DisplayName("Pbkdf2")
class Pbkdf2Test {

    // ─────────────────────────────────────────────────────────────────
    // generarSalt
    // ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("generarSalt devuelve un String no nulo y no vacío")
    void generarSalt_not_null_not_blank() {
        assertThat(Pbkdf2.generarSalt()).isNotBlank();
    }

    @Test
    @DisplayName("generarSalt es Base64 válido de 16 bytes")
    void generarSalt_is_valid_base64_16_bytes() {
        String salt = Pbkdf2.generarSalt();
        byte[] decoded = Base64.getDecoder().decode(salt);
        assertThat(decoded).hasSize(16);
    }

    @Test
    @DisplayName("Dos salts consecutivos son distintos (aleatoriedad)")
    void generarSalt_produces_unique_values() {
        assertThat(Pbkdf2.generarSalt()).isNotEqualTo(Pbkdf2.generarSalt());
    }

    // ─────────────────────────────────────────────────────────────────
    // derivarKey (char[])
    // ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("derivarKey devuelve byte[] de 32 bytes (AES-256)")
    void derivarKey_returns_32_bytes() throws Exception {
        String salt = Pbkdf2.generarSalt();
        byte[] key  = Pbkdf2.derivarKey("password".toCharArray(), salt);
        assertThat(key).hasSize(32);
    }

    @Test
    @DisplayName("derivarKey con mismo password+salt produce la misma clave (determinista)")
    void derivarKey_deterministic() throws Exception {
        String salt = Pbkdf2.generarSalt();
        byte[] key1 = Pbkdf2.derivarKey("mismaContrasenya".toCharArray(), salt);
        byte[] key2 = Pbkdf2.derivarKey("mismaContrasenya".toCharArray(), salt);
        assertThat(key1).isEqualTo(key2);
    }

    @Test
    @DisplayName("derivarKey con distintos passwords produce claves distintas")
    void derivarKey_different_passwords_produce_different_keys() throws Exception {
        String salt = Pbkdf2.generarSalt();
        byte[] key1 = Pbkdf2.derivarKey("password1".toCharArray(), salt);
        byte[] key2 = Pbkdf2.derivarKey("password2".toCharArray(), salt);
        assertThat(key1).isNotEqualTo(key2);
    }

    @Test
    @DisplayName("derivarKey con distinto salt produce clave distinta (anti-rainbow)")
    void derivarKey_different_salts_produce_different_keys() throws Exception {
        String salt1 = Pbkdf2.generarSalt();
        String salt2 = Pbkdf2.generarSalt();
        byte[] key1  = Pbkdf2.derivarKey("mismaContrasenya".toCharArray(), salt1);
        byte[] key2  = Pbkdf2.derivarKey("mismaContrasenya".toCharArray(), salt2);
        assertThat(key1).isNotEqualTo(key2);
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "contraseña_con_ñ", "P@ssw0rd!1234567890"})
    @DisplayName("derivarKey acepta variados passwords")
    void derivarKey_accepts_various_passwords(String pwd) throws Exception {
        String salt = Pbkdf2.generarSalt();
        byte[] key  = Pbkdf2.derivarKey(pwd.toCharArray(), salt);
        assertThat(key).hasSize(32);
    }

    // ─────────────────────────────────────────────────────────────────
    // derivarKey (String, deprecated)
    // ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("API deprecated (String) produce mismo resultado que char[]")
    @SuppressWarnings("deprecation")
    void derivarKey_deprecated_string_matches_chararray() throws Exception {
        String salt = Pbkdf2.generarSalt();
        byte[] fromChars  = Pbkdf2.derivarKey("testPwd".toCharArray(), salt);
        byte[] fromString = Pbkdf2.derivarKey("testPwd", salt);
        assertThat(fromChars).isEqualTo(fromString);
    }

    // ─────────────────────────────────────────────────────────────────
    // Integración: derivarKey + Encriptacion
    // ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Clave derivada sirve para cifrar y descifrar correctamente")
    void derivedKey_works_with_encryption() throws Exception {
        String salt     = Pbkdf2.generarSalt();
        byte[] key      = Pbkdf2.derivarKey("masterPassword!".toCharArray(), salt);
        String cifrado  = Encriptacion.encrypt(key, "mi secreto");
        String descifrado = Encriptacion.decrypt(key, cifrado);
        assertThat(descifrado).isEqualTo("mi secreto");
    }

    @Test
    @DisplayName("Clave derivada con password incorrecto NO descifra el texto")
    void wrongDerivedKey_cannot_decrypt() throws Exception {
        String salt     = Pbkdf2.generarSalt();
        byte[] keyCorr  = Pbkdf2.derivarKey("correcta".toCharArray(), salt);
        byte[] keyWrong = Pbkdf2.derivarKey("incorrecta".toCharArray(), salt);

        String cifrado    = Encriptacion.encrypt(keyCorr, "secreto");
        String descifrado = Encriptacion.decrypt(keyWrong, cifrado);
        assertThat(descifrado).isNull();
    }
}
