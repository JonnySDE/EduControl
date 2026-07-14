package com.educontrol.modelos;

import java.time.LocalDateTime;

public class Periodo {
    private int idPeriodo;
    private String estado; // 'Abierto' o 'Cerrado'
    private LocalDateTime fechaCierre;
    private String periodo;
    private int idGrupo;
    private int idCampoFormativo;

    public Periodo() {
    }

    public Periodo(int idPeriodo, String estado, LocalDateTime fechaCierre, String periodo,
                    int idGrupo, int idCampoFormativo) {
        this.idPeriodo = idPeriodo;
        this.estado = estado;
        this.fechaCierre = fechaCierre;
        this.periodo = periodo;
        this.idGrupo = idGrupo;
        this.idCampoFormativo = idCampoFormativo;
    }

    public int getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(int idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public int getIdCampoFormativo() {
        return idCampoFormativo;
    }

    public void setIdCampoFormativo(int idCampoFormativo) {
        this.idCampoFormativo = idCampoFormativo;
    }
}