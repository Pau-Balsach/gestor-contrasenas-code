package com.mycompany.gestorcontrasenyas.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mycompany.gestorcontrasenyas.db.SupabaseAuth;
import com.mycompany.gestorcontrasenyas.model.Categoria;
import com.mycompany.gestorcontrasenyas.utils.Config;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class CategoriaService {

    private static final String URL      = Config.get("supabase.url") + "/rest/v1";
    private static final String ANON_KEY = Config.get("supabase.anon_key");

    // Nombre reservado que activa el campo riot_id aunque el usuario
    // haya creado la categoría manualmente con es_riot = true.
    public static final String NOMBRE_RIOT = "Riot account";

    // ----------------------------------------------------------------
    // Obtener todas las categorías del usuario autenticado
    // ----------------------------------------------------------------
    public static List<Categoria> obtenerCategorias() {
        List<Categoria> lista = new ArrayList<>();
        try {
            String error = SupabaseAuth.asegurarTokenVigente();
            if (error != null) return lista;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/categorias?select=id,nombre,es_riot&order=nombre.asc"))
                    .header("apikey", ANON_KEY)
                    .header("Authorization", "Bearer " + SupabaseAuth.getAccessToken())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonArray array = JsonParser.parseString(response.body()).getAsJsonArray();
                for (JsonElement el : array) {
                    JsonObject obj = el.getAsJsonObject();
                    long    id     = obj.get("id").getAsLong();
                    String  nombre = obj.get("nombre").getAsString();
                    boolean esRiot = obj.has("es_riot") && !obj.get("es_riot").isJsonNull()
                                     && obj.get("es_riot").getAsBoolean();
                    lista.add(new Categoria(id, nombre, esRiot));
                }
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo categorías: " + e.getMessage());
        }
        return lista;
    }

    // ----------------------------------------------------------------
    // Obtener solo los nombres, para el combo de filtro en ConsultarUsuario
    // ----------------------------------------------------------------
    public static List<String> obtenerNombres() {
        List<String> nombres = new ArrayList<>();
        for (Categoria c : obtenerCategorias()) {
            nombres.add(c.getNombre());
        }
        return nombres;
    }

    // ----------------------------------------------------------------
    // Crear una nueva categoría
    // Devuelve null si fue bien, o el mensaje de error.
    // ----------------------------------------------------------------
    public static String crearCategoria(String nombre, boolean esRiot) {
        nombre = nombre.trim();
        if (nombre.isEmpty()) return "El nombre no puede estar vacío.";

        try {
            String error = SupabaseAuth.asegurarTokenVigente();
            if (error != null) return error;

            JsonObject obj = new JsonObject();
            obj.addProperty("user_id", SupabaseAuth.getUserId());
            obj.addProperty("nombre",  nombre);
            obj.addProperty("es_riot", esRiot);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/categorias"))
                    .header("Content-Type", "application/json")
                    .header("apikey", ANON_KEY)
                    .header("Authorization", "Bearer " + SupabaseAuth.getAccessToken())
                    .POST(HttpRequest.BodyPublishers.ofString(obj.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) return null;
            if (response.body().contains("categorias_user_nombre_unique")) {
                return "Ya existe una categoría con ese nombre.";
            }
            return "Error al crear categoría: " + response.body();

        } catch (Exception e) {
            return "Error de conexión: " + e.getMessage();
        }
    }

    // ----------------------------------------------------------------
    // Eliminar una categoría por nombre
    // Las cuentas que usaban esa categoría conservan el texto tal cual
    // (la columna 'categoria' en 'cuentas' es TEXT libre, sin FK).
    // ----------------------------------------------------------------
    public static String eliminarCategoria(String nombre) {
        try {
            String error = SupabaseAuth.asegurarTokenVigente();
            if (error != null) return error;

            String nombreEnc = java.net.URLEncoder.encode(nombre, "UTF-8");
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "/categorias?nombre=eq." + nombreEnc))
                    .header("apikey", ANON_KEY)
                    .header("Authorization", "Bearer " + SupabaseAuth.getAccessToken())
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) return null;
            return "Error al eliminar categoría: " + response.body();

        } catch (Exception e) {
            return "Error de conexión: " + e.getMessage();
        }
    }
}