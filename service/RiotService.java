package com.mycompany.gestorcontrasenyas.service;

import com.mycompany.gestorcontrasenyas.utils.Config;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class RiotService {

    private static final String HENRIK_KEY = Config.get("henrik.api_key");
    private static final String API_KEY    = Config.get("riot.api_key");
    private static final String ROUTING    = "europe"; 
    private static final String REGION_LOL = "euw1";   
    private static final String REGION_VAL = "eu";     


    public static String obtenerRangoValorant(String riotId) {
        try {
            String[] partes = riotId.split("#");
            if (partes.length != 2) return "Sin rango";

            String gameNameEnc = java.net.URLEncoder.encode(partes[0], "UTF-8");
            String tagLineEnc  = java.net.URLEncoder.encode(partes[1], "UTF-8");

            HttpClient client = HttpClient.newHttpClient();
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.henrikdev.xyz/valorant/v3/mmr/" + REGION_VAL + "/pc/" + gameNameEnc + "/" + tagLineEnc))
                    .header("Authorization", HENRIK_KEY)
                    .GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 1. Manejo de errores de conexión/API
            if (response.statusCode() == 429) return "Error: API Limit (429)";
            if (response.statusCode() == 404) {
                if (response.body() != null && response.body().contains("\"code\":24")) {
                    return "Sin rango (Inactivo)";
                }
                return "No encontrado";
            }
            if (response.statusCode() != 200) return "Sin rango";

            return extraerRangoDesdeMmrV3(response.body());

        } catch (Exception e) {
            return "Error";
        }
    }

    private static String extraerRangoDesdeMmrV3(String body) {
        try {
            JsonObject root = JsonParser.parseString(body).getAsJsonObject();
            if (!root.has("data") || root.get("data").isJsonNull()) return "Sin rango";
            JsonObject data = root.getAsJsonObject("data");

            // PRIORIDAD 1: Rango Actual
            if (data.has("current") && !data.get("current").isJsonNull()) {
                JsonObject tier = data.getAsJsonObject("current").getAsJsonObject("tier");
                if (esRangoValido(tier)) return tier.get("name").getAsString();
            }

            // PRIORIDAD 2: Último rango de temporadas pasadas
            if (data.has("seasonal") && !data.get("seasonal").isJsonNull()) {
                JsonArray seasonal = data.getAsJsonArray("seasonal");
                for (int i = seasonal.size() - 1; i >= 0; i--) {
                    JsonObject s = seasonal.get(i).getAsJsonObject();
                    if (s.has("end_tier") && !s.get("end_tier").isJsonNull()) {
                        JsonObject et = s.getAsJsonObject("end_tier");
                        if (esRangoValido(et)) {
                            String shortName = s.has("season") ? " (" + s.getAsJsonObject("season").get("short").getAsString() + ")" : "";
                            return et.get("name").getAsString() + shortName;
                        }
                    }
                }
            }            
        } catch (Exception e) {
            System.out.println("Error parseando MMR V3");
        }
        return "Sin rango";
    }

    private static boolean esRangoValido(JsonObject tier) {
        if (tier == null || tier.isJsonNull()) return false;
        return tier.get("id").getAsInt() > 1;
    }

    public static String obtenerRangoLol(String puuid) {
        if (puuid == null || puuid.isEmpty()) return "Sin rango";
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://" + REGION_LOL + ".api.riotgames.com/lol/league/v4/entries/by-puuid/" + puuid))
                    .header("X-Riot-Token", API_KEY)
                    .GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonArray leagues = JsonParser.parseString(response.body()).getAsJsonArray();
                if (leagues.size() == 0) return "Sin rango";

                for (JsonElement el : leagues) {
                    JsonObject entry = el.getAsJsonObject();
                    if ("RANKED_SOLO_5x5".equals(entry.get("queueType").getAsString())) {
                        return entry.get("tier").getAsString() + " " + entry.get("rank").getAsString() + " - " + entry.get("leaguePoints").getAsInt() + " LP";
                    }
                }
                JsonObject first = leagues.get(0).getAsJsonObject();
                return first.get("tier").getAsString() + " " + first.get("rank").getAsString();
            }
            return "Sin rango";
        } catch (Exception e) { return "Error LoL"; }
    }

    public static String obtenerPuuid(String riotId) {
        try {
            String[] partes = riotId.split("#");
            String gameName = java.net.URLEncoder.encode(partes[0], "UTF-8");
            String tagLine  = java.net.URLEncoder.encode(partes[1], "UTF-8");

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://" + ROUTING + ".api.riotgames.com/riot/account/v1/accounts/by-riot-id/" + gameName + "/" + tagLine))
                    .header("X-Riot-Token", API_KEY).GET().build();

            HttpResponse<String> res = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() == 200) return JsonParser.parseString(res.body()).getAsJsonObject().get("puuid").getAsString();
        } catch (Exception e) {}
        return null;
    }
}