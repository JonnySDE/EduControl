package com.educontrol.modelos;

public class ConfigExamen {
    private int idExamen;
    private int porcentaje;
    private int idUsuario;

    public ConfigExamen() {
    }

    public ConfigExamen(int idExamen, int porcentaje, int idUsuario) {
        this.idExamen = idExamen;
        this.porcentaje = porcentaje;
        this.idUsuario = idUsuario;
    }

    public int getIdExamen() {
        return idExamen;
    }

    public void setIdExamen(int idExamen) {
        this.idExamen = idExamen;
    }

    public int getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(int porcentaje) {
        this.porcentaje = porcentaje;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}