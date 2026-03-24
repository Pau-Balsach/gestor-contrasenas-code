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
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class CuentaService {

    private static final String URL      = Config.get("supabase.url") + "/rest/v1";
    private static final String ANON_KEY = Config.get("supabase.anon_key");

    public static String guardarCuenta(String usuario, String password, String categoria, String riotId) {
        try {
            byte[] keyBytes    = SupabaseAuth.getMasterKey();
            String usuarioEnc  = Encriptacion.encrypt(keyBytes, usuario);
            String passwordEnc = Encriptacion.encrypt(keyBytes, password);

            // Gson construye el JSON correctamente, incluyendo null para riot_id
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

            String body = obj.toString();

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/cuentas"))
                    .header("Content-Type", "application/json")
                    .header("apikey", ANON_KEY)
                    .header("Authorization", "Bearer " + SupabaseAuth.getAccessToken())
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) return null;
            return "Error al guardar: " + response.body();

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public static List<Cuenta> obtenerCuentas() {
        List<Cuenta> lista = new ArrayList<>();
        try {
            byte[] keyBytes = SupabaseAuth.getMasterKey();

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/cuentas?select=categoria,usuario,password,riot_id"))
                    .header("apikey", ANON_KEY)
                    .header("Authorization", "Bearer " + SupabaseAuth.getAccessToken())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String json = response.body();
                if (json.equals("[]")) return lista;

                // Gson parsea el array JSON correctamente, incluyendo valores null
                JsonArray array = JsonParser.parseString(json).getAsJsonArray();
                for (JsonElement elemento : array) {
                    JsonObject registro = elemento.getAsJsonObject();

                    String categoria   = registro.get("categoria").getAsString();
                    String usuarioEnc  = registro.get("usuario").getAsString();
                    String passwordEnc = registro.get("password").getAsString();

                    // riot_id puede ser null en la base de datos
                    String riotId = null;
                    JsonElement riotElement = registro.get("riot_id");
                    if (riotElement != null && !riotElement.isJsonNull()) {
                        riotId = riotElement.getAsString();
                    }

                    String usuarioDec  = Encriptacion.decrypt(keyBytes, usuarioEnc);
                    String passwordDec = Encriptacion.decrypt(keyBytes, passwordEnc);

                    if (usuarioDec != null && passwordDec != null) {
                        lista.add(new Cuenta(categoria, usuarioDec, passwordDec, riotId));
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error obtenerCuentas: " + e.getMessage());
        }
        return lista;
    }
    public static String getCuentaid(String usuario_buscar)
    {
        String cuentaid = "";
        try{
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
                    if (usuarioDec.equals(usuario_buscar))
                    {
                        cuentaid   = registro.get("id").getAsString();
                        break;
                    }
                }
            }
        }
        catch(Exception e){
            System.out.println("Error getCuentaid: " + e.getMessage());
        }
        return cuentaid;
    }

    public static void eliminarCuenta(String cuenta_id) {
        try {
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
                System.out.println("Error al eliminar. Código: " + response.statusCode() + " - " + response.body());
            }
            
        } catch (Exception e) {
            System.out.println("Error eliminarCuenta: " + e.getMessage());
        }
    }
    public static void actualizarCuenta(String cuenta_id, String nuevo_usuario, String nuevo_password) {
        try {
            byte[] keyBytes = SupabaseAuth.getMasterKey();
            String nuevo_usuario_enc = Encriptacion.encrypt(keyBytes, nuevo_usuario);
            String nuevo_password_enc = Encriptacion.encrypt(keyBytes, nuevo_password);
            HttpClient client = HttpClient.newHttpClient();

            JsonObject obj = new JsonObject();
            obj.addProperty("usuario", nuevo_usuario_enc);
            obj.addProperty("password", nuevo_password_enc);
            String jsonBody = obj.toString();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/cuentas?id=eq." + cuenta_id))
                    .header("apikey", ANON_KEY)
                    .header("Authorization", "Bearer " + SupabaseAuth.getAccessToken())
                    .header("Content-Type", "application/json") 
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println("Cuenta actualizada con éxito.");
            } else {
                System.out.println("Error al actualizar: " + response.statusCode() + " - " + response.body());
            }

        } catch (Exception e) {
            System.out.println("Error en actualizarCuenta: " + e.getMessage());
        }
    }
}