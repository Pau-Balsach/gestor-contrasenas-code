package com.mycompany.gestorcontrasenyas.model;

public class Categoria {

    private final long   id;
    private final String nombre;
    private final boolean esRiot;

    public Categoria(long id, String nombre, boolean esRiot) {
        this.id     = id;
        this.nombre = nombre;
        this.esRiot = esRiot;
    }

    public long    getId()     { return id; }
    public String  getNombre() { return nombre; }
    public boolean isEsRiot()  { return esRiot; }

    /** El combo muestra solo el nombre. */
    @Override
    public String toString() { return nombre; }
}