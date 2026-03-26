package com.mycompany.gestorcontrasenyas.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mycompany.gestorcontrasenyas.db.SupabaseAuth;
import com.mycompany.gestorcontrasenyas.model.Cuenta;
import com.mycompany.gestorcontrasenyas.utils.Config;
import com.mycompany.gestorcontrasenyas.utils.Encriptacion;
import java.net.URI;
import java.util.Arrays;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class CuentaService {

    private static final String URL      = Config.get("supabase.url") + "/rest/v1";
    private static final String ANON_KEY = Config.get("supabase.anon_key");

    private static String validarSesion() {
        if (!SupabaseAuth.haySesionActiva()) return "No hay sesión activa.";
        return SupabaseAuth.asegurarTokenVigente();
    }

    public static String guardarCuenta(String usuario, char[] passwordChars, String categoria, String riotId) {
        try {
            String errorSesion = validarSesion();
            if (errorSesion != null) return errorSesion;
            byte[] keyBytes    = SupabaseAuth.getMasterKey();
            String usuarioEnc  = Encriptacion.encrypt(keyBytes, usuario);
            String passwordEnc = Encriptacion.encrypt(keyBytes, passwordChars);

            JsonObject obj = new JsonObject();
            obj.addProperty("user_id",   SupabaseAuth.getUserId());
            obj.addProperty("usuario",   usuarioEnc);
            obj.addProperty("password",  passwordEnc);
            obj.addProperty("categoria", categoria);

            boolean tieneRiotId = categoria.equals("Riot account") && riotId != null && !riotId.isEmpty();
            if (tieneRiotId) {
                obj.addProperty("riot_id", riotId);
            } else {
                obj.add("riot_id", com.google.gson.JsonNull.INSTANCE);
            }

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/cuentas"))
                    .header("Content-Type", "application/json")
                    .header("apikey", ANON_KEY)
                    .header("Authorization", "Bearer " + SupabaseAuth.getAccessToken())
                    .POST(HttpRequest.BodyPublishers.ofString(obj.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) return null;
            return "Error al guardar: " + response.body();

        } catch (Exception e) {
            return "Error al guardar la cuenta.";
        } finally {
            if (passwordChars != null) Arrays.fill(passwordChars, '\0');
        }
    }

    public static List<Cuenta> obtenerCuentas() {
        List<Cuenta> lista = new ArrayList<>();
        try {
            String errorSesion = validarSesion();
            if (errorSesion != null) return lista;
            byte[] keyBytes = SupabaseAuth.getMasterKey();

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/cuentas?select=id,categoria,usuario,password,riot_id"))
                    .header("apikey", ANON_KEY)
                    .header("Authorization", "Bearer " + SupabaseAuth.getAccessToken())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String json = response.body();
                if (json.equals("[]")) return lista;

                JsonArray array = JsonParser.parseString(json).getAsJsonArray();
                for (JsonElement elemento : array) {
                    JsonObject registro = elemento.getAsJsonObject();

                    String id          = registro.get("id").getAsString();
                    String categoria   = registro.get("categoria").getAsString();
                    String usuarioEnc  = registro.get("usuario").getAsString();
                    String passwordEnc = registro.get("password").getAsString();

                    String riotId = null;
                    JsonElement riotElement = registro.get("riot_id");
                    if (riotElement != null && !riotElement.isJsonNull()) {
                        riotId = riotElement.getAsString();
                    }

                    String usuarioDec = Encriptacion.decrypt(keyBytes, usuarioEnc);
                    if (usuarioDec != null) {
                        // Evitar exponer contraseña real en memoria/UI: solo máscara para listado.
                        lista.add(new Cuenta(categoria, usuarioDec, "********", riotId));

                        boolean legacyUsuario = !usuarioEnc.startsWith("v2:");
                        boolean legacyPassword = !passwordEnc.startsWith("v2:");
                        if (legacyUsuario || legacyPassword) {
                            String passwordDec = Encriptacion.decrypt(keyBytes, passwordEnc);
                            if (passwordDec != null) {
                                migrarCuentaAV2(id, keyBytes, usuarioDec, passwordDec);
                            }
                            passwordDec = null;
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error obteniendo cuentas.");
        }
        return lista;
    }

    private static void migrarCuentaAV2(String cuentaId, byte[] keyBytes, String usuarioDec, String passwordDec) {
        try {
            String usuarioV2 = Encriptacion.encrypt(keyBytes, usuarioDec);
            String passwordV2 = Encriptacion.encrypt(keyBytes, passwordDec);

            JsonObject obj = new JsonObject();
            obj.addProperty("usuario", usuarioV2);
            obj.addProperty("password", passwordV2);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/cuentas?id=eq." + cuentaId))
                    .header("apikey", ANON_KEY)
                    .header("Authorization", "Bearer " + SupabaseAuth.getAccessToken())
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(obj.toString()))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ignored) {
            // Si falla la migración transparente, no bloqueamos la lectura de cuentas.
        }
    }

    public static String obtenerPasswordDescifradaPorId(String cuentaId) {
        try {
            String errorSesion = validarSesion();
            if (errorSesion != null) return null;
            byte[] keyBytes = SupabaseAuth.getMasterKey();
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/cuentas?id=eq." + cuentaId + "&select=password"))
                    .header("apikey", ANON_KEY)
                    .header("Authorization", "Bearer " + SupabaseAuth.getAccessToken())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String json = response.body();
                if (json.equals("[]")) return null;
                JsonArray array = JsonParser.parseString(json).getAsJsonArray();
                String passwordEnc = array.get(0).getAsJsonObject().get("password").getAsString();
                return Encriptacion.decrypt(keyBytes, passwordEnc);
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo contraseña.");
        }
        return null;
    }

    public static String getCuentaid(String usuario_buscar)
    {
        String cuentaid = "";
        try{
            String errorSesion = validarSesion();
            if (errorSesion != null) return cuentaid;
            byte[] keyBytes = SupabaseAuth.getMasterKey();
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/cuentas?select=id,usuario"))
                    .header("apikey", ANON_KEY)
                    .header("Authorization", "Bearer " + SupabaseAuth.getAccessToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String json = response.body();
                JsonArray array = JsonParser.parseString(json).getAsJsonArray();
                for (JsonElement elemento : array) {
                    JsonObject registro = elemento.getAsJsonObject();

                    String usuarioEnc  = registro.get("usuario").getAsString();
                    String usuarioDec  = Encriptacion.decrypt(keyBytes, usuarioEnc);
                    if (usuarioDec != null && usuarioDec.equals(usuario_buscar))
                    {
                        cuentaid   = registro.get("id").getAsString();
                        break;
                    }
                }
            }
        }
        catch(Exception e){
            System.out.println("Error buscando cuenta.");
        }
        return cuentaid;
    }

    public static void eliminarCuenta(String cuenta_id) {
        try {
            String errorSesion = validarSesion();
            if (errorSesion != null) return;
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/cuentas?id=eq." + cuenta_id))
                    .header("apikey", ANON_KEY)
                    .header("Authorization", "Bearer " + SupabaseAuth.getAccessToken())
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println("Cuenta eliminada correctamente.");
            } else {
                System.out.println("No se pudo eliminar la cuenta.");
            }

        } catch (Exception e) {
            System.out.println("Error eliminando cuenta.");
        }
    }

    /**
     * Actualiza usuario, password y opcionalmente riot_id de una cuenta.
     *
     * @param cuenta_id            ID de la cuenta a actualizar.
     * @param nuevo_usuario        Nuevo nombre de usuario (en claro, se cifrará).
     * @param nuevo_password_chars Nueva contraseña como char[] (se cifrará y limpiará).
     * @param nuevo_riot_id        Nuevo Riot ID si es Riot account, o null para no modificarlo.
     */
    public static void actualizarCuenta(String cuenta_id, String nuevo_usuario, char[] nuevo_password_chars, String nuevo_riot_id) {
        try {
            String errorSesion = validarSesion();
            if (errorSesion != null) return;
            byte[] keyBytes = SupabaseAuth.getMasterKey();
            String nuevo_usuario_enc  = Encriptacion.encrypt(keyBytes, nuevo_usuario);
            String nuevo_password_enc = Encriptacion.encrypt(keyBytes, nuevo_password_chars);

            JsonObject obj = new JsonObject();
            obj.addProperty("usuario",  nuevo_usuario_enc);
            obj.addProperty("password", nuevo_password_enc);

            // Solo incluimos riot_id en el PATCH si se proporcionó un valor
            if (nuevo_riot_id != null) {
                obj.addProperty("riot_id", nuevo_riot_id);
            }

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/cuentas?id=eq." + cuenta_id))
                    .header("apikey", ANON_KEY)
                    .header("Authorization", "Bearer " + SupabaseAuth.getAccessToken())
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(obj.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println("Cuenta actualizada con éxito.");
            } else {
                System.out.println("No se pudo actualizar la cuenta.");
            }

        } catch (Exception e) {
            System.out.println("Error actualizando cuenta.");
        } finally {
            if (nuevo_password_chars != null) Arrays.fill(nuevo_password_chars, '\0');
        }
    }
}