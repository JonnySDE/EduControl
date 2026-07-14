package com.educontrol.modelos;

public class ConfigAsistencia {
    private int idConfigAsistencia;
    private int porcentaje;
    private int idUsuario;

    public ConfigAsistencia() {
    }

    public ConfigAsistencia(int idConfigAsistencia, int porcentaje, int idUsuario) {
        this.idConfigAsistencia = idConfigAsistencia;
        this.porcentaje = porcentaje;
        this.idUsuario = idUsuario;
    }

    public int getIdConfigAsistencia() {
        return idConfigAsistencia;
    }

    public void setIdConfigAsistencia(int idConfigAsistencia) {
        this.idConfigAsistencia = idConfigAsistencia;
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