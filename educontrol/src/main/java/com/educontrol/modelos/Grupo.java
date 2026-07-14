package com.educontrol.modelos;

public class Grupo {
    private int idGrupo;
    private int grado;
    private String grupo;

    public Grupo() {
    }

    public Grupo(int idGrupo, int grado, String grupo) {
        this.idGrupo = idGrupo;
        this.grado = grado;
        this.grupo = grupo;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public int getGrado() {
        return grado;
    }

    public void setGrado(int grado) {
        this.grado = grado;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }
}