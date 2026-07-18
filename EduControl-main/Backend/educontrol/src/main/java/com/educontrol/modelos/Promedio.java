package com.educontrol.modelos;

import java.math.BigDecimal;

public class Promedio {
    private int idPromedio;
    private int matricula;
    private int idCampoFormativo;
    private BigDecimal promedioFinal;
    private int grado;
    private int idPeriodo;

    public Promedio() {
    }

    public Promedio(int idPromedio, int matricula, int idCampoFormativo,
                     BigDecimal promedioFinal, int grado, int idPeriodo) {
        this.idPromedio = idPromedio;
        this.matricula = matricula;
        this.idCampoFormativo = idCampoFormativo;
        this.promedioFinal = promedioFinal;
        this.grado = grado;
        this.idPeriodo = idPeriodo;
    }

    public int getIdPromedio() {
        return idPromedio;
    }

    public void setIdPromedio(int idPromedio) {
        this.idPromedio = idPromedio;
    }

    public int getMatricula() {
        return matricula;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public int getIdCampoFormativo() {
        return idCampoFormativo;
    }

    public void setIdCampoFormativo(int idCampoFormativo) {
        this.idCampoFormativo = idCampoFormativo;
    }

    public BigDecimal getPromedioFinal() {
        return promedioFinal;
    }

    public void setPromedioFinal(BigDecimal promedioFinal) {
        this.promedioFinal = promedioFinal;
    }

    public int getGrado() {
        return grado;
    }

    public void setGrado(int grado) {
        this.grado = grado;
    }

    public int getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(int idPeriodo) {
        this.idPeriodo = idPeriodo;
    }
}