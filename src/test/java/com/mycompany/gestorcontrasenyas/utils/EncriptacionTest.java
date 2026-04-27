package com.mycompany.gestorcontrasenyas.utils;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests para Encriptacion (AES-GCM v2 + compatibilidad AES-CBC v1).
 *
 * No requieren red ni base de datos: son tests unitarios puros de criptografía.
 */
@DisplayName("Encriptacion")
class EncriptacionTest {

    /** Clave AES-256 de 32 bytes fija para tests. */
    private static final byte[] KEY_32 = new byte[32];

    static {
        for (int i = 0; i < 32; i++) KEY_32[i] = (byte) (i + 1);
    }

    // ─────────────────────────────────────────────────────────────────
    // encrypt(byte[], String)
    // ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("encrypt(String) produce prefijo v2:")
    void encrypt_string_produces_v2_prefix() {
        String cifrado = Encriptacion.encrypt(KEY_32, "hola mundo");
        assertThat(cifrado).startsWith("v2:");
    }

    @Test
    @DisplayName("encrypt(String) null -> null")
    void encrypt_null_string_returns_null() {
        assertThat(Encriptacion.encrypt(KEY_32, (String) null)).isNull();
    }

    @Test
    @DisplayName("Cifrar la misma cadena dos veces produce resultados distintos (IV aleatorio)")
    void encrypt_same_value_produces_different_ciphertext() {
        String a = Encriptacion.encrypt(KEY_32, "test");
        String b = Encriptacion.encrypt(KEY_32, "test");
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    @DisplayName("encrypt -> decrypt round-trip con String")
    void encrypt_decrypt_roundtrip_string() {
        String plaintext = "contraseña_super_secreta!";
        String cifrado   = Encriptacion.encrypt(KEY_32, plaintext);
        String descifrado = Encriptacion.decrypt(KEY_32, cifrado);
        assertThat(descifrado).isEqualTo(plaintext);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "a", "contraseña con espacios", "emoji 🔐"})
    @DisplayName("Round-trip con varios contenidos")
    void encrypt_decrypt_various_values(String value) {
        String descifrado = Encriptacion.decrypt(KEY_32, Encriptacion.encrypt(KEY_32, value));
        assertThat(descifrado).isEqualTo(value);
    }

    @Test
    @DisplayName("Round-trip con cadena muy larga (1000 chars)")
    void encrypt_decrypt_very_long_string() {
        String value = "muy largo ".repeat(100);
        String descifrado = Encriptacion.decrypt(KEY_32, Encriptacion.encrypt(KEY_32, value));
        assertThat(descifrado).isEqualTo(value);
    }

    // ─────────────────────────────────────────────────────────────────
    // encrypt(byte[], char[])
    // ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("encrypt(char[]) null -> null")
    void encrypt_null_chararray_returns_null() {
        assertThat(Encriptacion.encrypt(KEY_32, (char[]) null)).isNull();
    }

    @Test
    @DisplayName("encrypt(char[]) -> decrypt round-trip")
    void encrypt_decrypt_chararray() {
        char[] pwd = "P@ssw0rd!".toCharArray();
        String cifrado    = Encriptacion.encrypt(KEY_32, pwd);
        String descifrado = Encriptacion.decrypt(KEY_32, cifrado);
        assertThat(descifrado).isEqualTo("P@ssw0rd!");
    }

    @Test
    @DisplayName("encrypt(char[]) produce prefijo v2:")
    void encrypt_chararray_v2_prefix() {
        assertThat(Encriptacion.encrypt(KEY_32, "x".toCharArray())).startsWith("v2:");
    }

    // ─────────────────────────────────────────────────────────────────
    // decrypt
    // ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("decrypt null -> null")
    void decrypt_null_returns_null() {
        assertThat(Encriptacion.decrypt(KEY_32, null)).isNull();
    }

    @Test
    @DisplayName("decrypt con clave incorrecta -> null (GCM lanza excepción internamente)")
    void decrypt_wrong_key_returns_null() {
        byte[] otherKey = new byte[32];
        Arrays.fill(otherKey, (byte) 0xFF);

        String cifrado    = Encriptacion.encrypt(KEY_32, "secreto");
        String descifrado = Encriptacion.decrypt(otherKey, cifrado);
        assertThat(descifrado).isNull();
    }

    @Test
    @DisplayName("decrypt dato corrupto -> null")
    void decrypt_corrupted_data_returns_null() {
        String basura = "v2:ZXN0b25vZXN1bnRleHRvdmFsaWRv";
        assertThat(Encriptacion.decrypt(KEY_32, basura)).isNull();
    }

    @Test
    @DisplayName("decrypt cadena vacía -> null (demasiado corta para GCM IV)")
    void decrypt_empty_string_returns_null() {
        // Cadena sin prefijo conocido ni contenido suficiente
        assertThat(Encriptacion.decrypt(KEY_32, "v2:")).isNull();
    }

    // ─────────────────────────────────────────────────────────────────
    // Compatibilidad v1 (CBC legacy)
    // ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Valores con prefijo v1: se descifran con CBC (compatibilidad backward)")
    void decrypt_v1_prefix_uses_cbc() throws Exception {
        // Construimos manualmente un cifrado v1 usando CBC para simular datos legacy
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] ivBytes = new byte[16];
        new java.security.SecureRandom().nextBytes(ivBytes);
        javax.crypto.spec.SecretKeySpec keySpec = new javax.crypto.spec.SecretKeySpec(KEY_32, "AES");
        javax.crypto.spec.IvParameterSpec ivSpec = new javax.crypto.spec.IvParameterSpec(ivBytes);
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encrypted = cipher.doFinal("legacyValue".getBytes(java.nio.charset.StandardCharsets.UTF_8));

        byte[] combined = new byte[16 + encrypted.length];
        System.arraycopy(ivBytes, 0, combined, 0, 16);
        System.arraycopy(encrypted, 0, combined, 16, encrypted.length);

        String v1Cifrado = "v1:" + java.util.Base64.getEncoder().encodeToString(combined);
        assertThat(Encriptacion.decrypt(KEY_32, v1Cifrado)).isEqualTo("legacyValue");
    }

    @Test
    @DisplayName("Valor sin prefijo (legacy puro) se trata como v1 (CBC)")
    void decrypt_no_prefix_treated_as_v1() throws Exception {
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] ivBytes = new byte[16];
        new java.security.SecureRandom().nextBytes(ivBytes);
        javax.crypto.spec.SecretKeySpec keySpec = new javax.crypto.spec.SecretKeySpec(KEY_32, "AES");
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, keySpec, new javax.crypto.spec.IvParameterSpec(ivBytes));
        byte[] encrypted = cipher.doFinal("sinPrefijo".getBytes(java.nio.charset.StandardCharsets.UTF_8));

        byte[] combined = new byte[16 + encrypted.length];
        System.arraycopy(ivBytes, 0, combined, 0, 16);
        System.arraycopy(encrypted, 0, combined, 16, encrypted.length);

        String noPrefijo = java.util.Base64.getEncoder().encodeToString(combined);
        assertThat(Encriptacion.decrypt(KEY_32, noPrefijo)).isEqualTo("sinPrefijo");
    }

    // ─────────────────────────────────────────────────────────────────
    // Seguridad: la clave NO debe aparecer en el cifrado
    // ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("El ciphertext no contiene bytes de la clave en texto plano")
    void ciphertext_does_not_leak_key() {
        String cifrado = Encriptacion.encrypt(KEY_32, "secreto");
        // El texto cifrado es Base64 y no debería contener la representación hex de la clave
        assertThat(cifrado).doesNotContain("0102030405060708");
    }
}