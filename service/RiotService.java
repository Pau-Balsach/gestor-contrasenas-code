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
            System.out.println("Error obtenerPuuid: " + e.getMessage());
            return null;
        }
    }

    public static String obtenerRangoValorant(String riotId) {
        try {
            String[] partes = riotId.split("#");
            if (partes.length != 2) return "Sin rango";

            String gameName = java.net.URLEncoder.encode(partes[0], "UTF-8");
            String tagLine  = java.net.URLEncoder.encode(partes[1], "UTF-8");

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.henrikdev.xyz/valorant/v2/mmr/eu/" + gameName + "/" + tagLine))
                    .header("Accept", "application/json")
                    .header("Authorization", HENRIK_KEY)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String tier = extractJson(response.body(), "currenttierpatched");
                if (tier != null) return tier;
            }
            return "Sin rango";

        } catch (Exception e) {
            System.out.println("Error obtenerRangoValorant: " + e.getMessage());
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
            System.out.println("Error obtenerRangoLol: " + e.getMessage());
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