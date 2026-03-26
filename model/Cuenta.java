package com.mycompany.gestorcontrasenyas.model;

public class Cuenta {
    private String categoria;
    private String usuario;
    private String password;
    private String riotId;

    public Cuenta(String categoria, String usuario, String password, String riotId) {
        this.categoria = categoria;
        this.usuario = usuario;
        this.password = password;
        this.riotId = riotId;
    }

    public String getCategoria() { return categoria; }
    public String getUsuario() { return usuario; }
    public String getPassword() { return password; }
    public String getRiotId() { return riotId; }
}