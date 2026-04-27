package com.mycompany.gestorcontrasenyas.utils;

import java.awt.Frame;
import javax.swing.JFrame;

public final class EstadoVentanaSesion {

    private static final Object LOCK = new Object();
    private static boolean maximizadaPreferida = false;
    private static boolean inicializado = false;

    private EstadoVentanaSesion() {
    }

    public static void instalar(JFrame frame) {
        if (frame.getRootPane().getClientProperty(EstadoVentanaSesion.class) != null) {
            return;
        }
        frame.getRootPane().putClientProperty(EstadoVentanaSesion.class, Boolean.TRUE);
        frame.addWindowStateListener(event -> guardarPreferencia((event.getNewState() & Frame.MAXIMIZED_BOTH) != 0));
        aplicar(frame);
    }

    public static void aplicar(JFrame frame) {
        boolean maximizar;
        synchronized (LOCK) {
            maximizar = inicializado && maximizadaPreferida;
        }
        int estadoActual = frame.getExtendedState();
        int nuevoEstado = maximizar
                ? (estadoActual | Frame.MAXIMIZED_BOTH)
                : (estadoActual & ~Frame.MAXIMIZED_BOTH);
        if (estadoActual != nuevoEstado) {
            frame.setExtendedState(nuevoEstado);
        }
    }

    public static void guardarPreferencia(boolean maximizada) {
        synchronized (LOCK) {
            inicializado = true;
            maximizadaPreferida = maximizada;
        }
    }

    public static void reiniciar() {
        synchronized (LOCK) {
            inicializado = false;
            maximizadaPreferida = false;
        }
    }
}
