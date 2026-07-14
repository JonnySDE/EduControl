package com.educontrol.modelos;

public class ConfigDisciplina {
    private int idConfigDisciplina;
    private int porcentaje;
    private int idUsuario;

    public ConfigDisciplina() {
    }

    public ConfigDisciplina(int idConfigDisciplina, int porcentaje, int idUsuario) {
        this.idConfigDisciplina = idConfigDisciplina;
        this.porcentaje = porcentaje;
        this.idUsuario = idUsuario;
    }

    public int getIdConfigDisciplina() {
        return idConfigDisciplina;
    }

    public void setIdConfigDisciplina(int idConfigDisciplina) {
        this.idConfigDisciplina = idConfigDisciplina;
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