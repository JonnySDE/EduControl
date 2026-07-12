package com.educontrol.modelos;

import java.time.LocalDate;

public class RegistroDisciplina {
    private int idRegistroDisciplina;
    private String observaciones;
    private String comportamiento;
    private LocalDate fecha;
    private int matricula;
    private int idConfigDisciplina;
    private int idUsuario;
    private int idPeriodo;

    public RegistroDisciplina() {
    }

    public RegistroDisciplina(int idRegistroDisciplina, String observaciones, String comportamiento,
                               LocalDate fecha, int matricula, int idConfigDisciplina, int idUsuario, int idPeriodo) {
        this.idRegistroDisciplina = idRegistroDisciplina;
        this.observaciones = observaciones;
        this.comportamiento = comportamiento;
        this.fecha = fecha;
        this.matricula = matricula;
        this.idConfigDisciplina = idConfigDisciplina;
        this.idUsuario = idUsuario;
        this.idPeriodo = idPeriodo;
    }

    public int getIdRegistroDisciplina() {
        return idRegistroDisciplina;
    }

    public void setIdRegistroDisciplina(int idRegistroDisciplina) {
        this.idRegistroDisciplina = idRegistroDisciplina;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getComportamiento() {
        return comportamiento;
    }

    public void setComportamiento(String comportamiento) {
        this.comportamiento = comportamiento;
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

    public int getIdConfigDisciplina() {
        return idConfigDisciplina;
    }

    public void setIdConfigDisciplina(int idConfigDisciplina) {
        this.idConfigDisciplina = idConfigDisciplina;
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