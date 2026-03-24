package com.mycompany.gestorcontrasenyas.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Carga la configuracion desde config.properties.
 * Este fichero debe estar en el mismo directorio que el .jar
 * y NO debe subirse a ningun repositorio.
 */
public class Config {

    private static final Properties props = new Properties();
    private static boolean loaded = false;

    private static void cargar() {
        if (loaded) return;
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
            loaded = true;
        } catch (IOException e) {
            System.err.println("ERROR: No se encontro config.properties.");
            System.err.println("Crea el fichero config.properties junto al .jar con las claves necesarias.");
            System.exit(1); // Sin configuracion la app no puede funcionar
        }
    }

    public static String get(String clave) {
        cargar();
        String valor = props.getProperty(clave);
        if (valor == null) {
            System.err.println("ERROR: Falta la clave '" + clave + "' en config.properties");
            System.exit(1);
        }
        return valor;
    }
}