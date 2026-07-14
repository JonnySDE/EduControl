package com.educontrol.modelos;

public class ConfigTarea {
    private int idTarea;
    private int porcentaje;
    private int idUsuario;

    public ConfigTarea() {
    }

    public ConfigTarea(int idTarea, int porcentaje, int idUsuario) {
        this.idTarea = idTarea;
        this.porcentaje = porcentaje;
        this.idUsuario = idUsuario;
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
}