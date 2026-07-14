package com.educontrol.modelos;

public class ConfigTarea {
    private int idTarea;
    private int porcentaje;
    private int idUsuario;
    private int idCampoFormativo;

    public ConfigTarea() {
    }

    public ConfigTarea(int idTarea, int porcentaje, int idUsuario, int idCampoFormativo) {
        this.idTarea = idTarea;
        this.porcentaje = porcentaje;
        this.idUsuario = idUsuario;
        this.idCampoFormativo = idCampoFormativo;
    }

    public int getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(int idTarea) {
        this.idTarea = idTarea;
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

    public int getIdCampoFormativo() {
        return idCampoFormativo;
    }

    public void setIdCampoFormativo(int idCampoFormativo) {
        this.idCampoFormativo = idCampoFormativo;
    }
}