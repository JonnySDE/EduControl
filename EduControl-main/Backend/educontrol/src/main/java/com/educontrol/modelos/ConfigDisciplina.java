package com.educontrol.modelos;

public class ConfigDisciplina {
    private int idConfigDisciplina;
    private int porcentaje;
    private int idCampoFormativo;
    private int idUsuario;

    public ConfigDisciplina() {
    }

    public ConfigDisciplina(int idConfigDisciplina, int porcentaje, int idCampoFormativo, int idUsuario) {
        this.idConfigDisciplina = idConfigDisciplina;
        this.porcentaje = porcentaje;
        this.idCampoFormativo = idCampoFormativo;
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