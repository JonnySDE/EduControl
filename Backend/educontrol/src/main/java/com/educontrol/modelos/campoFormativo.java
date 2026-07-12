package com.educontrol.modelos;

public class CampoFormativo {
    private int idCampoFormativo;
    private String nombre;
    private int grado;
    private String cicloEscolar;

    public CampoFormativo() {
    }

    public CampoFormativo(int idCampoFormativo, String nombre, int grado, String cicloEscolar) {
        this.idCampoFormativo = idCampoFormativo;
        this.nombre = nombre;
        this.grado = grado;
        this.cicloEscolar = cicloEscolar;
    }

    public int getIdCampoFormativo() {
        return idCampoFormativo;
    }

    public void setIdCampoFormativo(int idCampoFormativo) {
        this.idCampoFormativo = idCampoFormativo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getGrado() {
        return grado;
    }

    public void setGrado(int grado) {
        this.grado = grado;
    }

    public String getCicloEscolar() {
        return cicloEscolar;
    }

    public void setCicloEscolar(String cicloEscolar) {
        this.cicloEscolar = cicloEscolar;
    }
}