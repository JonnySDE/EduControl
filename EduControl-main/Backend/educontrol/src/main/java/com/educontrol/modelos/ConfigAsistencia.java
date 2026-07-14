package com.educontrol.modelos;

public class ConfigAsistencia {
    private int idConfigAsistencia;
    private int porcentaje;
    private int idCampoFormativo;
    private int idUsuario;

    public ConfigAsistencia() {
    }

    public ConfigAsistencia(int idConfigAsistencia, int porcentaje, int idCampoFormativo, int idUsuario) {
        this.idConfigAsistencia = idConfigAsistencia;
        this.porcentaje = porcentaje;
        this.idCampoFormativo = idCampoFormativo;
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

    public int getIdCampoFormativo() {
        return idCampoFormativo;
    }

    public void setIdCampoFormativo(int idCampoFormativo) {
        this.idCampoFormativo = idCampoFormativo;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}