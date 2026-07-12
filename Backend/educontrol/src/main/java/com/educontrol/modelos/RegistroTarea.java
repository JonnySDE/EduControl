package com.educontrol.modelos;

public class RegistroTarea {
    private int idRegistroTarea;
    private String nombre;
    private String observaciones;
    private String estatus;
    private int matricula;
    private int idTarea;
    private int idUsuario;
    private int idPeriodo;

    public RegistroTarea() {
    }

    public RegistroTarea(int idRegistroTarea, String nombre, String observaciones, String estatus,
                          int matricula, int idTarea, int idUsuario, int idPeriodo) {
        this.idRegistroTarea = idRegistroTarea;
        this.nombre = nombre;
        this.observaciones = observaciones;
        this.estatus = estatus;
        this.matricula = matricula;
        this.idTarea = idTarea;
        this.idUsuario = idUsuario;
        this.idPeriodo = idPeriodo;
    }

    public int getIdRegistroTarea() {
        return idRegistroTarea;
    }

    public void setIdRegistroTarea(int idRegistroTarea) {
        this.idRegistroTarea = idRegistroTarea;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public int getMatricula() {
        return matricula;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public int getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(int idTarea) {
        this.idTarea = idTarea;
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