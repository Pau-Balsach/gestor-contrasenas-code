package com.mycompany.gestorcontrasenyas.service;

import com.google.gson.JsonObject;
import com.mycompany.gestorcontrasenyas.db.SupabaseAuth;
import com.mycompany.gestorcontrasenyas.utils.Config;
import com.mycompany.gestorcontrasenyas.utils.Encriptacion;
import com.mycompany.gestorcontrasenyas.utils.HttpGateway;
import com.mycompany.gestorcontrasenyas.utils.Pbkdf2;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class AuthService {

    private static final String TEXTO_VERIFICACION = "gestor-contrasenyas-ok";
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(15);
    private static final HttpClient CLIENT = HttpGateway.client();

    public static String login(String email, char[] passwordChars) {
        return SupabaseAuth.login(email, passwordChars);
    }

    public static boolean tieneMasterKey(String userId) {
        String errorSesion = SupabaseAuth.asegurarTokenVigente();
        if (errorSesion != null) {
            return false;
        }
        String salt = SupabaseAuth.obtenerSalt(userId);
        return salt != null;
    }

    public static String configurarMasterKey(char[] masterPasswordChars) {
        String errorSesion = SupabaseAuth.asegurarTokenVigente();
        if (errorSesion != null) {
            return errorSesion;
        }

        String userId = SupabaseAuth.getUserId();
        JsonObject datos = SupabaseAuth.obtenerDatosUsuario(userId);

        try {
            if (datos == null) {
                String salt = Pbkdf2.generarSalt();
                byte[] key = Pbkdf2.derivarKey(masterPasswordChars, salt);
                String verificador = Encriptacion.encrypt(key, TEXTO_VERIFICACION);

                String error = SupabaseAuth.guardarUsuario(userId, salt, verificador);
                if (error != null) {
                    return error;
                }

                SupabaseAuth.setMasterKey(key);
                return null;
            }

            String salt = datos.get("salt").getAsString();
            String verificador = datos.has("verificador") && !datos.get("verificador").isJsonNull()
                    ? datos.get("verificador").getAsString()
                    : null;

            byte[] key = Pbkdf2.derivarKey(masterPasswordChars, salt);
            if (verificador != null) {
                String textoDescifrado = Encriptacion.decrypt(key, verificador);
                if (!TEXTO_VERIFICACION.equals(textoDescifrado)) {
                    return "Contraseña maestra incorrecta.";
                }
            }
            SupabaseAuth.setMasterKey(key);
            return null;
        } catch (Exception e) {
            return "Error al generar la clave.";
        }
    }

    public static String signup(String email, char[] passwordChars) {
        return SupabaseAuth.signup(email, passwordChars);
    }

    public static String enviarRecuperacion(String email) {
        try {
            JsonObject obj = new JsonObject();
            obj.addProperty("email", email);

            String supabaseUrl = Config.get("supabase.url");
            String anonKey = Config.get("supabase.anon_key");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(supabaseUrl + "/auth/v1/recover"))
                    .timeout(REQUEST_TIMEOUT)
                    .header("Content-Type", "application/json")
                    .header("apikey", anonKey)
                    .POST(HttpRequest.BodyPublishers.ofString(obj.toString()))
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return null;
            }
            return "Error al enviar el correo.";
        } catch (Exception e) {
            return "Error de conexión.";
        }
    }
}