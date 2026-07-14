package com.educontrol.modelos;

public class ConfigParticipacion {
    private int idParticipacion;
    private int porcentaje;
    private int idUsuario;

    public ConfigParticipacion() {
    }

    public ConfigParticipacion(int idParticipacion, int porcentaje, int idUsuario) {
        this.idParticipacion = idParticipacion;
        this.porcentaje = porcentaje;
        this.idUsuario = idUsuario;
    }

    public int getIdParticipacion() {
        return idParticipacion;
    }

    public void setIdParticipacion(int idParticipacion) {
        this.idParticipacion = idParticipacion;
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