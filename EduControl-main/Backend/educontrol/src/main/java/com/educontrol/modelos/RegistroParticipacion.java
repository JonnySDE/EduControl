package com.educontrol.modelos;

import java.time.LocalDate;

public class RegistroParticipacion {
    private int idRegistroParticipacion;
    private float puntuacion;
    private LocalDate fecha;
    private int matricula;
    private int idParticipacion;
    private int idUsuario;
    private int idPeriodo;

    public RegistroParticipacion() {
    }

    public RegistroParticipacion(int idRegistroParticipacion, float puntuacion, LocalDate fecha,
                                  int matricula, int idParticipacion, int idUsuario, int idPeriodo) {
        this.idRegistroParticipacion = idRegistroParticipacion;
        this.puntuacion = puntuacion;
        this.fecha = fecha;
        this.matricula = matricula;
        this.idParticipacion = idParticipacion;
        this.idUsuario = idUsuario;
        this.idPeriodo = idPeriodo;
    }

    public int getIdRegistroParticipacion() {
        return idRegistroParticipacion;
    }

    public void setIdRegistroParticipacion(int idRegistroParticipacion) {
        this.idRegistroParticipacion = idRegistroParticipacion;
    }

    public float getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(float puntuacion) {
        this.puntuacion = puntuacion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public int getMatricula() {
        return matricula;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public int getIdParticipacion() {
        return idParticipacion;
    }

    public void setIdParticipacion(int idParticipacion) {
        this.idParticipacion = idParticipacion;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(int idPeriodo) {
        this.idPeriodo = idPeriodo;
    }
}