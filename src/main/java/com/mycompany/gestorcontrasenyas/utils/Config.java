package com.mycompany.gestorcontrasenyas.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

/**
 * Carga la configuracion desde variables de entorno o config.properties.
 * Prioridad:
 *  1) Variable de entorno (ej: SUPABASE_URL)
 *  2) config.properties junto al .jar
 */
public class Config {

    private static final Properties props = new Properties();
    private static boolean loaded = false;

    private static void cargar() {
        if (loaded) return;

        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
        } catch (IOException ignored) {
            // Permitimos funcionar solo con variables de entorno.
        }
        loaded = true;
    }

    private static String toEnvKey(String clave) {
        return clave.replace('.', '_').toUpperCase(Locale.ROOT);
    }

    public static String get(String clave) {
        cargar();

        String envKey = toEnvKey(clave);
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.isBlank()) return envVal;

        String valor = props.getProperty(clave);
        if (valor == null || valor.isBlank()) {
            System.err.println("ERROR: Falta la clave '" + clave + "'. Define " + envKey + " o config.properties.");
            System.exit(1);
        }
        return valor;
    }
}