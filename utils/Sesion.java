package com.mycompany.gestorcontrasenyas.utils;

import java.util.Arrays;

public class Sesion {

    private final String userId;
    private String accessToken;
    private String refreshToken;
    private long accessTokenExpiraEnEpochSec;
    private byte[] masterKey;

    public Sesion(String userId, String accessToken, String refreshToken, long accessTokenExpiraEnEpochSec) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiraEnEpochSec = accessTokenExpiraEnEpochSec;
    }

    public String getUserId() {
        return userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public long getAccessTokenExpiraEnEpochSec() {
        return accessTokenExpiraEnEpochSec;
    }

    public synchronized void actualizarTokens(String nuevoAccessToken, String nuevoRefreshToken, long nuevoExpiraEnEpochSec) {
        this.accessToken = nuevoAccessToken;
        this.refreshToken = nuevoRefreshToken;
        this.accessTokenExpiraEnEpochSec = nuevoExpiraEnEpochSec;
    }

    public synchronized void setMasterKey(byte[] key) {
        limpiarMasterKey();
        this.masterKey = key != null ? Arrays.copyOf(key, key.length) : null;
    }

    public synchronized byte[] getMasterKey() {
        return masterKey != null ? Arrays.copyOf(masterKey, masterKey.length) : null;
    }

    public synchronized void limpiarMasterKey() {
        if (masterKey != null) {
            Arrays.fill(masterKey, (byte) 0);
            masterKey = null;
        }
    }

    public synchronized void destruir() {
        limpiarMasterKey();
        accessToken = null;
        refreshToken = null;
        accessTokenExpiraEnEpochSec = 0;
    }
}