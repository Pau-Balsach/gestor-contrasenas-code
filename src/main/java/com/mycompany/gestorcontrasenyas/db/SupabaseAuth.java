package com.mycompany.gestorcontrasenyas.db;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mycompany.gestorcontrasenyas.utils.Config;
import com.mycompany.gestorcontrasenyas.utils.HttpGateway;
import com.mycompany.gestorcontrasenyas.utils.Sesion;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;

public class SupabaseAuth {

    private static final String URL = Config.get("supabase.url");
    private static final String ANON_KEY = Config.get("supabase.anon_key");
    private static final long TOKEN_BUFFER_RENOVACION_SEC = 120L;
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(15);
    private static final HttpClient CLIENT = HttpGateway.client();

    private static Sesion sesionActual = null;

    public static synchronized void setMasterKey(byte[] key) {
        if (sesionActual != null) {
            sesionActual.setMasterKey(key);
        }
    }

    public static synchronized byte[] getMasterKey() {
        return sesionActual != null ? sesionActual.getMasterKey() : null;
    }

    public static synchronized String getAccessToken() {
        return sesionActual != null ? sesionActual.getAccessToken() : null;
    }

    public static synchronized String getUserId() {
        return sesionActual != null ? sesionActual.getUserId() : null;
    }

    public static synchronized boolean haySesionActiva() {
        return sesionActual != null && sesionActual.getAccessToken() != null;
    }

    public static synchronized void cerrarSesion() {
        if (sesionActual != null) {
            sesionActual.destruir();
            sesionActual = null;
        }
    }

    public static String login(String email, char[] passwordChars) {
        try {
            JsonObject obj = new JsonObject();
            obj.addProperty("email", email);
            obj.addProperty("password", new String(passwordChars));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/auth/v1/token?grant_type=password"))
                    .timeout(REQUEST_TIMEOUT)
                    .header("Content-Type", "application/json")
                    .header("apikey", ANON_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(obj.toString()))
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                String accessToken = json.get("access_token").getAsString();
                String refreshToken = json.has("refresh_token") && !json.get("refresh_token").isJsonNull()
                        ? json.get("refresh_token").getAsString()
                        : null;
                long expiresIn = json.has("expires_in") && !json.get("expires_in").isJsonNull()
                        ? json.get("expires_in").getAsLong()
                        : 3600L;
                long expiraEnEpochSec = (System.currentTimeMillis() / 1000L) + expiresIn;
                String userId = json.getAsJsonObject("user").get("id").getAsString();

                synchronized (SupabaseAuth.class) {
                    if (sesionActual != null) {
                        sesionActual.destruir();
                    }
                    sesionActual = new Sesion(userId, accessToken, refreshToken, expiraEnEpochSec);
                }
                return null;
            }
            return "Email o contraseña incorrectos.";
        } catch (Exception e) {
            return "Error de conexión.";
        } finally {
            if (passwordChars != null) {
                Arrays.fill(passwordChars, '\0');
            }
        }
    }

    public static String renovarToken() {
        Sesion sesion;
        synchronized (SupabaseAuth.class) {
            sesion = sesionActual;
        }

        if (sesion == null || sesion.getRefreshToken() == null || sesion.getRefreshToken().isBlank()) {
            return "No hay sesión para renovar token.";
        }

        try {
            JsonObject obj = new JsonObject();
            obj.addProperty("refresh_token", sesion.getRefreshToken());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/auth/v1/token?grant_type=refresh_token"))
                    .timeout(REQUEST_TIMEOUT)
                    .header("Content-Type", "application/json")
                    .header("apikey", ANON_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(obj.toString()))
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                String accessToken = json.get("access_token").getAsString();
                String refreshToken = json.has("refresh_token") && !json.get("refresh_token").isJsonNull()
                        ? json.get("refresh_token").getAsString()
                        : sesion.getRefreshToken();
                long expiresIn = json.has("expires_in") && !json.get("expires_in").isJsonNull()
                        ? json.get("expires_in").getAsLong()
                        : 3600L;
                long expiraEnEpochSec = (System.currentTimeMillis() / 1000L) + expiresIn;
                sesion.actualizarTokens(accessToken, refreshToken, expiraEnEpochSec);
                return null;
            }
            return "No se pudo renovar token.";
        } catch (Exception e) {
            return "Error al renovar token.";
        }
    }

    public static String asegurarTokenVigente() {
        Sesion sesion;
        synchronized (SupabaseAuth.class) {
            sesion = sesionActual;
        }

        if (sesion == null) {
            return "No hay sesión activa.";
        }

        long ahora = System.currentTimeMillis() / 1000L;
        if (sesion.getAccessTokenExpiraEnEpochSec() - ahora <= TOKEN_BUFFER_RENOVACION_SEC) {
            return renovarToken();
        }
        return null;
    }

    public static String signup(String email, char[] passwordChars) {
        try {
            JsonObject obj = new JsonObject();
            obj.addProperty("email", email);
            obj.addProperty("password", new String(passwordChars));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/auth/v1/signup"))
                    .timeout(REQUEST_TIMEOUT)
                    .header("Content-Type", "application/json")
                    .header("apikey", ANON_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(obj.toString()))
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return null;
            }
            if (response.body().contains("weak_password")) {
                return "La contraseña debe tener al menos 6 caracteres.";
            }
            if (response.body().contains("already registered")) {
                return "El email ya está en uso.";
            }
            return "Error al registrarse.";
        } catch (Exception e) {
            return "Error de conexión.";
        } finally {
            if (passwordChars != null) {
                Arrays.fill(passwordChars, '\0');
            }
        }
    }

    public static String guardarUsuario(String userId, String salt, String verificador) {
        try {
            JsonObject obj = new JsonObject();
            obj.addProperty("id", userId);
            obj.addProperty("salt", salt);
            obj.addProperty("verificador", verificador);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/rest/v1/usuarios"))
                    .timeout(REQUEST_TIMEOUT)
                    .header("Content-Type", "application/json")
                    .header("apikey", ANON_KEY)
                    .header("Authorization", "Bearer " + getAccessToken())
                    .POST(HttpRequest.BodyPublishers.ofString(obj.toString()))
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 201) {
                return null;
            }
            return "Error al guardar usuario.";
        } catch (Exception e) {
            return "Error de conexión.";
        }
    }

    public static JsonObject obtenerDatosUsuario(String userId) {
        try {
            String userIdEnc = URLEncoder.encode(userId, StandardCharsets.UTF_8);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/rest/v1/usuarios?id=eq." + userIdEnc + "&select=salt,verificador"))
                    .timeout(REQUEST_TIMEOUT)
                    .header("apikey", ANON_KEY)
                    .header("Authorization", "Bearer " + getAccessToken())
                    .GET()
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String body = response.body();
                if (body.equals("[]")) {
                    return null;
                }
                JsonArray array = JsonParser.parseString(body).getAsJsonArray();
                return array.get(0).getAsJsonObject();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String obtenerSalt(String userId) {
        JsonObject datos = obtenerDatosUsuario(userId);
        if (datos == null) {
            return null;
        }
        return datos.has("salt") && !datos.get("salt").isJsonNull()
                ? datos.get("salt").getAsString()
                : null;
    }
}