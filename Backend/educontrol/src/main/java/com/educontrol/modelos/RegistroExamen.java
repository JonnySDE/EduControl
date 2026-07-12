package com.educontrol.modelos;

import java.time.LocalDate;

public class RegistroExamen {
    private int idRegistroExamen;
    private LocalDate fecha;
    private String nombreExamen;
    private int matricula;
    private int idExamen;
    private int idUsuario;
    private int idCampoFormativo;
    private int idPeriodo;

    public RegistroExamen() {
    }

    public RegistroExamen(int idRegistroExamen, LocalDate fecha, String nombreExamen,
                           int matricula, int idExamen, int idUsuario, int idCampoFormativo, int idPeriodo) {
        this.idRegistroExamen = idRegistroExamen;
        this.fecha = fecha;
        this.nombreExamen = nombreExamen;
        this.matricula = matricula;
        this.idExamen = idExamen;
        this.idUsuario = idUsuario;
        this.idCampoFormativo = idCampoFormativo;
        this.idPeriodo = idPeriodo;
    }

    public int getIdRegistroExamen() {
        return idRegistroExamen;
    }

    public void setIdRegistroExamen(int idRegistroExamen) {
        this.idRegistroExamen = idRegistroExamen;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getNombreExamen() {
        return nombreExamen;
    }

    public void setNombreExamen(String nombreExamen) {
        this.nombreExamen = nombreExamen;
    }

    public int getMatricula() {
        return matricula;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public int getIdExamen() {
        return idExamen;
    }

    public void setIdExamen(int idExamen) {
        this.idExamen = idExamen;
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

    public int getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(int idPeriodo) {
        this.idPeriodo = idPeriodo;
    }
}