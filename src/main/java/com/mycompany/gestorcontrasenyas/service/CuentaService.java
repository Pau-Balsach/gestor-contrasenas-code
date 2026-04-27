package com.mycompany.gestorcontrasenyas.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mycompany.gestorcontrasenyas.db.SupabaseAuth;
import com.mycompany.gestorcontrasenyas.model.Cuenta;
import com.mycompany.gestorcontrasenyas.utils.Config;
import com.mycompany.gestorcontrasenyas.utils.Encriptacion;
import com.mycompany.gestorcontrasenyas.utils.HttpGateway;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CuentaService {

    private static final String URL = Config.get("supabase.url") + "/rest/v1";
    private static final String ANON_KEY = Config.get("supabase.anon_key");
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(15);
    private static final HttpClient CLIENT = HttpGateway.client();
    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$");

    private static String validarSesion() {
        if (!SupabaseAuth.haySesionActiva()) {
            return "No hay sesión activa.";
        }
        return SupabaseAuth.asegurarTokenVigente();
    }

    private static boolean isUuid(String value) {
        return value != null && UUID_PATTERN.matcher(value).matches();
    }

    public static String guardarCuenta(String usuario, char[] passwordChars, String categoria, String riotId) {
        try {
            String errorSesion = validarSesion();
            if (errorSesion != null) {
                return errorSesion;
            }
            byte[] keyBytes = SupabaseAuth.getMasterKey();
            String usuarioEnc = Encriptacion.encrypt(keyBytes, usuario);
            String passwordEnc = Encriptacion.encrypt(keyBytes, passwordChars);

            JsonObject obj = new JsonObject();
            obj.addProperty("user_id", SupabaseAuth.getUserId());
            obj.addProperty("usuario", usuarioEnc);
            obj.addProperty("password", passwordEnc);
            obj.addProperty("categoria", categoria);

            boolean tieneRiotId = "Riot account".equals(categoria) && riotId != null && !riotId.isEmpty();
            if (tieneRiotId) {
                obj.addProperty("riot_id", riotId);
            } else {
                obj.add("riot_id", com.google.gson.JsonNull.INSTANCE);
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/cuentas"))
                    .timeout(REQUEST_TIMEOUT)
                    .header("Content-Type", "application/json")
                    .header("apikey", ANON_KEY)
                    .header("Authorization", "Bearer " + SupabaseAuth.getAccessToken())
                    .POST(HttpRequest.BodyPublishers.ofString(obj.toString()))
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 201) {
                return null;
            }
            return "Error al guardar.";
        } catch (Exception e) {
            return "Error al guardar la cuenta.";
        } finally {
            if (passwordChars != null) {
                Arrays.fill(passwordChars, '\0');
            }
        }
    }

    public static List<Cuenta> obtenerCuentas() {
        List<Cuenta> lista = new ArrayList<>();
        try {
            String errorSesion = validarSesion();
            if (errorSesion != null) {
                return lista;
            }
            byte[] keyBytes = SupabaseAuth.getMasterKey();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/cuentas?select=id,categoria,usuario,password,riot_id"))
                    .timeout(REQUEST_TIMEOUT)
                    .header("apikey", ANON_KEY)
                    .header("Authorization", "Bearer " + SupabaseAuth.getAccessToken())
                    .GET()
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String json = response.body();
                if (json.equals("[]")) {
                    return lista;
                }

                JsonArray array = JsonParser.parseString(json).getAsJsonArray();
                for (JsonElement elemento : array) {
                    JsonObject registro = elemento.getAsJsonObject();
                    String id = registro.get("id").getAsString();
                    String categoria = registro.get("categoria").getAsString();
                    String usuarioEnc = registro.get("usuario").getAsString();
                    String passwordEnc = registro.get("password").getAsString();

                    String riotId = null;
                    JsonElement riotElement = registro.get("riot_id");
                    if (riotElement != null && !riotElement.isJsonNull()) {
                        riotId = riotElement.getAsString();
                    }

                    String usuarioDec = Encriptacion.decrypt(keyBytes, usuarioEnc);
                    if (usuarioDec != null) {
                        lista.add(new Cuenta(categoria, usuarioDec, "********", riotId));

                        boolean legacyUsuario = !usuarioEnc.startsWith("v2:");
                        boolean legacyPassword = !passwordEnc.startsWith("v2:");
                        if (legacyUsuario || legacyPassword) {
                            String passwordDec = Encriptacion.decrypt(keyBytes, passwordEnc);
                            if (passwordDec != null && isUuid(id)) {
                                migrarCuentaAV2(id, keyBytes, usuarioDec, passwordDec);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            return lista;
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

            String idEnc = URLEncoder.encode(cuentaId, StandardCharsets.UTF_8);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/cuentas?id=eq." + idEnc))
                    .timeout(REQUEST_TIMEOUT)
                    .header("apikey", ANON_KEY)
                    .header("Authorization", "Bearer " + SupabaseAuth.getAccessToken())
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(obj.toString()))
                    .build();

            CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
        }
    }

    public static String obtenerPasswordDescifradaPorId(String cuentaId) {
        if (!isUuid(cuentaId)) {
            return null;
        }
        try {
            String errorSesion = validarSesion();
            if (errorSesion != null) {
                return null;
            }
            byte[] keyBytes = SupabaseAuth.getMasterKey();
            String cuentaIdEnc = URLEncoder.encode(cuentaId, StandardCharsets.UTF_8);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/cuentas?id=eq." + cuentaIdEnc + "&select=password"))
                    .timeout(REQUEST_TIMEOUT)
                    .header("apikey", ANON_KEY)
                    .header("Authorization", "Bearer " + SupabaseAuth.getAccessToken())
                    .GET()
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String json = response.body();
                if (json.equals("[]")) {
                    return null;
                }
                JsonArray array = JsonParser.parseString(json).getAsJsonArray();
                String passwordEnc = array.get(0).getAsJsonObject().get("password").getAsString();
                return Encriptacion.decrypt(keyBytes, passwordEnc);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static String getCuentaId(String usuarioBuscar) {
        String cuentaId = "";
        try {
            String errorSesion = validarSesion();
            if (errorSesion != null) {
                return cuentaId;
            }
            byte[] keyBytes = SupabaseAuth.getMasterKey();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/cuentas?select=id,usuario"))
                    .timeout(REQUEST_TIMEOUT)
                    .header("apikey", ANON_KEY)
                    .header("Authorization", "Bearer " + SupabaseAuth.getAccessToken())
                    .GET()
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonArray array = JsonParser.parseString(response.body()).getAsJsonArray();
                for (JsonElement elemento : array) {
                    JsonObject registro = elemento.getAsJsonObject();
                    String usuarioEnc = registro.get("usuario").getAsString();
                    String usuarioDec = Encriptacion.decrypt(keyBytes, usuarioEnc);
                    if (usuarioDec != null && usuarioDec.equals(usuarioBuscar)) {
                        String foundId = registro.get("id").getAsString();
                        if (isUuid(foundId)) {
                            cuentaId = foundId;
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            return cuentaId;
        }
        return cuentaId;
    }

    public static String getCuentaid(String usuarioBuscar) {
        return getCuentaId(usuarioBuscar);
    }

    public static void eliminarCuenta(String cuentaId) {
        if (!isUuid(cuentaId)) {
            return;
        }
        try {
            String errorSesion = validarSesion();
            if (errorSesion != null) {
                return;
            }

            String cuentaIdEnc = URLEncoder.encode(cuentaId, StandardCharsets.UTF_8);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/cuentas?id=eq." + cuentaIdEnc))
                    .timeout(REQUEST_TIMEOUT)
                    .header("apikey", ANON_KEY)
                    .header("Authorization", "Bearer " + SupabaseAuth.getAccessToken())
                    .DELETE()
                    .build();

            CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
        }
    }

    public static void actualizarCuenta(String cuentaId, String nuevoUsuario, char[] nuevoPasswordChars, String nuevoRiotId) {
        if (!isUuid(cuentaId)) {
            return;
        }
        try {
            String errorSesion = validarSesion();
            if (errorSesion != null) {
                return;
            }
            byte[] keyBytes = SupabaseAuth.getMasterKey();
            String nuevoUsuarioEnc = Encriptacion.encrypt(keyBytes, nuevoUsuario);
            String nuevoPasswordEnc = Encriptacion.encrypt(keyBytes, nuevoPasswordChars);

            JsonObject obj = new JsonObject();
            obj.addProperty("usuario", nuevoUsuarioEnc);
            obj.addProperty("password", nuevoPasswordEnc);
            if (nuevoRiotId != null) {
                obj.addProperty("riot_id", nuevoRiotId);
            }

            String cuentaIdEnc = URLEncoder.encode(cuentaId, StandardCharsets.UTF_8);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/cuentas?id=eq." + cuentaIdEnc))
                    .timeout(REQUEST_TIMEOUT)
                    .header("apikey", ANON_KEY)
                    .header("Authorization", "Bearer " + SupabaseAuth.getAccessToken())
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(obj.toString()))
                    .build();

            CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
        } finally {
            if (nuevoPasswordChars != null) {
                Arrays.fill(nuevoPasswordChars, '\0');
            }
        }
    }
}