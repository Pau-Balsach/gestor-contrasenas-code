package com.mycompany.gestorcontrasenyas.service;

import com.mycompany.gestorcontrasenyas.utils.Config;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RiotService {

    private static final String HENRIK_KEY = Config.get("henrik.api_key");
    private static final String API_KEY    = Config.get("riot.api_key");
    private static final String ROUTING    = "europe";
    private static final String REGION     = "euw1";

    public static String obtenerPuuid(String riotId) {
        try {
            String[] partes = riotId.split("#");
            if (partes.length != 2) return null;

            String gameName = java.net.URLEncoder.encode(partes[0], "UTF-8");
            String tagLine  = java.net.URLEncoder.encode(partes[1], "UTF-8");

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://" + ROUTING + ".api.riotgames.com/riot/account/v1/accounts/by-riot-id/" + gameName + "/" + tagLine))
                    .header("X-Riot-Token", API_KEY)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) return extractJson(response.body(), "puuid");
            return null;

        } catch (Exception e) {
            System.out.println("Error obteniendo puuid.");
            return null;
        }
    }

    /**
     * Obtiene el rango de Valorant usando el endpoint v3 de HenrikDev.
     * Si el rango actual es Unrated, busca en el historial de temporadas
     * el rango mas reciente conocido y lo devuelve con la temporada entre parentesis.
     */
    public static String obtenerRangoValorant(String riotId) {
        try {
            String[] partes = riotId.split("#");
            if (partes.length != 2) return "Sin rango";

            String gameName = java.net.URLEncoder.encode(partes[0], "UTF-8");
            String tagLine  = java.net.URLEncoder.encode(partes[1], "UTF-8");

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.henrikdev.xyz/valorant/v3/mmr/eu/pc/" + gameName + "/" + tagLine))
                    .header("Accept", "application/json")
                    .header("Authorization", HENRIK_KEY)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return "Sin rango";

            com.google.gson.JsonObject json = com.google.gson.JsonParser
                    .parseString(response.body()).getAsJsonObject();

            if (!json.has("data") || json.get("data").isJsonNull()) return "Sin rango";
            com.google.gson.JsonObject data = json.getAsJsonObject("data");

            // Leer rango actual
            String rangoActual = null;
            int tierActualId   = -1;
            if (data.has("current") && !data.get("current").isJsonNull()) {
                com.google.gson.JsonObject current = data.getAsJsonObject("current");
                if (current.has("tier") && !current.get("tier").isJsonNull()) {
                    com.google.gson.JsonObject tier = current.getAsJsonObject("tier");
                    tierActualId = tier.has("id") ? tier.get("id").getAsInt() : -1;
                    rangoActual  = tier.has("name") ? tier.get("name").getAsString() : null;
                }
            }

            // Si el rango actual es valido (no Unrated: id > 1), devolverlo directamente
            boolean esUnrated = tierActualId <= 1 || "Unrated".equalsIgnoreCase(rangoActual);
            if (!esUnrated && rangoActual != null) {
                return rangoActual;
            }

            // Rango actual es Unrated: buscar en seasonal el rango mas reciente conocido.
            // El array viene ordenado de mas ANTIGUO a mas RECIENTE, asi que iteramos al reves.
            if (data.has("seasonal") && !data.get("seasonal").isJsonNull()) {
                com.google.gson.JsonArray seasonal = data.getAsJsonArray("seasonal");
                for (int i = seasonal.size() - 1; i >= 0; i--) {
                    com.google.gson.JsonObject temporada = seasonal.get(i).getAsJsonObject();
                    if (!temporada.has("end_tier") || temporada.get("end_tier").isJsonNull()) continue;

                    com.google.gson.JsonObject endTier = temporada.getAsJsonObject("end_tier");
                    int endId        = endTier.has("id")   ? endTier.get("id").getAsInt()     : -1;
                    String endNombre = endTier.has("name") ? endTier.get("name").getAsString() : null;

                    // Ignorar entradas que tambien sean Unrated
                    if (endId <= 1 || "Unrated".equalsIgnoreCase(endNombre) || endNombre == null) continue;

                    // Obtener la etiqueta de temporada
                    String etiqueta = " (temp. anterior)";
                    if (temporada.has("season") && !temporada.get("season").isJsonNull()) {
                        com.google.gson.JsonObject season = temporada.getAsJsonObject("season");
                        if (season.has("short") && !season.get("short").isJsonNull()) {
                            etiqueta = " (" + season.get("short").getAsString() + ")";
                        }
                    }

                    return endNombre + etiqueta;
                }
            }

            return "Sin rango";

        } catch (Exception e) {
            System.out.println("Error obteniendo rango de Valorant.");
            return "Error";
        }
    }

    public static String obtenerRangoLol(String puuid) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://" + REGION + ".api.riotgames.com/lol/league/v4/entries/by-puuid/" + puuid))
                    .header("X-Riot-Token", API_KEY)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String body = response.body();
                if (body.equals("[]")) return "Sin rango";
                String tier = extractJson(body, "tier");
                String rank = extractJson(body, "rank");
                String lp   = extractJson(body, "leaguePoints");
                if (tier != null) return tier + " " + rank + " - " + lp + " LP";
            }
            return "Sin rango";

        } catch (Exception e) {
            System.out.println("Error obteniendo rango de LoL.");
            return "Error";
        }
    }

    private static String extractJson(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) {
            search = "\"" + key + "\":";
            start = json.indexOf(search);
            if (start == -1) return null;
            start += search.length();
            int end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
            return json.substring(start, end).trim();
        }
        start += search.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}