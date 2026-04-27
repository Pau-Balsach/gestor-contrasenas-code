package com.mycompany.gestorcontrasenyas.utils;

import java.net.http.HttpClient;
import java.time.Duration;

public final class HttpGateway {

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .version(HttpClient.Version.HTTP_2)
            .build();

    private HttpGateway() {
    }

    public static HttpClient client() {
        return CLIENT;
    }
}