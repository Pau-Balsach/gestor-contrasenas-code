package com.mycompany.gestorcontrasenyas.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.security.SecureRandom;
import java.util.Base64;

public class Encriptacion {

    private static final int CBC_IV_SIZE = 16;
    private static final int GCM_IV_SIZE = 12;
    private static final int GCM_TAG_BITS = 128;

    private static final String PREFIX_V1 = "v1:";
    private static final String PREFIX_V2 = "v2:";

    public static String encrypt(byte[] keyBytes, String value) {
        if (value == null) return null;
        byte[] plainBytes = value.getBytes(StandardCharsets.UTF_8);
        try {
            return encryptBytesV2(keyBytes, plainBytes);
        } finally {
            Arrays.fill(plainBytes, (byte) 0);
        }
    }

    public static String encrypt(byte[] keyBytes, char[] valueChars) {
        if (valueChars == null) return null;

        ByteBuffer utf8Buffer = StandardCharsets.UTF_8.encode(CharBuffer.wrap(valueChars));
        byte[] plainBytes = new byte[utf8Buffer.remaining()];
        utf8Buffer.get(plainBytes);

        try {
            return encryptBytesV2(keyBytes, plainBytes);
        } finally {
            Arrays.fill(plainBytes, (byte) 0);
        }
    }

    private static String encryptBytesV2(byte[] keyBytes, byte[] plainBytes) {
        try {
            // v2 por defecto: AES-GCM (confidencialidad + integridad)
            byte[] ivBytes = new byte[GCM_IV_SIZE];
            new SecureRandom().nextBytes(ivBytes);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_BITS, ivBytes);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

            byte[] encrypted = cipher.doFinal(plainBytes);

            // Formato v2: [12 bytes nonce][ciphertext+tag], codificado Base64
            byte[] combined = new byte[GCM_IV_SIZE + encrypted.length];
            System.arraycopy(ivBytes, 0, combined, 0, GCM_IV_SIZE);
            System.arraycopy(encrypted, 0, combined, GCM_IV_SIZE, encrypted.length);

            return PREFIX_V2 + Base64.getEncoder().encodeToString(combined);
        } catch (Exception ex) {
            System.out.println("Error encrypt.");
            return null;
        }
    }

    public static String decrypt(byte[] keyBytes, String encryptedValue) {
        if (encryptedValue == null) return null;

        try {
            if (encryptedValue.startsWith(PREFIX_V2)) {
                return decryptV2(keyBytes, encryptedValue.substring(PREFIX_V2.length()));
            }
            if (encryptedValue.startsWith(PREFIX_V1)) {
                return decryptV1(keyBytes, encryptedValue.substring(PREFIX_V1.length()));
            }

            // Compatibilidad histórica: valores sin prefijo se interpretan como v1 (CBC)
            return decryptV1(keyBytes, encryptedValue);
        } catch (Exception ex) {
            System.out.println("Error decrypt.");
            return null;
        }
    }

    private static String decryptV2(byte[] keyBytes, String encryptedBase64) throws Exception {
        byte[] combined = Base64.getDecoder().decode(encryptedBase64);
        if (combined.length <= GCM_IV_SIZE) return null;

        byte[] ivBytes = new byte[GCM_IV_SIZE];
        byte[] encrypted = new byte[combined.length - GCM_IV_SIZE];
        System.arraycopy(combined, 0, ivBytes, 0, GCM_IV_SIZE);
        System.arraycopy(combined, GCM_IV_SIZE, encrypted, 0, encrypted.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_BITS, ivBytes);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

        byte[] original = cipher.doFinal(encrypted);
        return new String(original, StandardCharsets.UTF_8);
    }

    private static String decryptV1(byte[] keyBytes, String encryptedBase64) throws Exception {
        byte[] combined = Base64.getDecoder().decode(encryptedBase64);
        if (combined.length <= CBC_IV_SIZE) return null;

        byte[] ivBytes = new byte[CBC_IV_SIZE];
        byte[] encrypted = new byte[combined.length - CBC_IV_SIZE];
        System.arraycopy(combined, 0, ivBytes, 0, CBC_IV_SIZE);
        System.arraycopy(combined, CBC_IV_SIZE, encrypted, 0, encrypted.length);

        IvParameterSpec iv = new IvParameterSpec(ivBytes);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);

        byte[] original = cipher.doFinal(encrypted);
        return new String(original, StandardCharsets.UTF_8);
    }
}