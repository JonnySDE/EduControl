package com.educontrol.modelos;

import java.time.LocalDate;

public class RegistroAsistencia {
    private int idRegistroAsis;
    private String estado; // 'Asistencia', 'Falta', 'Permiso'
    private LocalDate fecha;
    private int matricula;
    private int idConfigAsistencia;
    private int idUsuario;

    public RegistroAsistencia() {
    }

    public RegistroAsistencia(int idRegistroAsis, String estado, LocalDate fecha,
                            int matricula, int idConfigAsistencia, int idUsuario) {
        this.idRegistroAsis = idRegistroAsis;
        this.estado = estado;
        this.fecha = fecha;
        this.matricula = matricula;
        this.idConfigAsistencia = idConfigAsistencia;
        this.idUsuario = idUsuario;
    }

    public int getIdRegistroAsis() {
        return idRegistroAsis;
    }

    public void setIdRegistroAsis(int idRegistroAsis) {
        this.idRegistroAsis = idRegistroAsis;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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

    public int getIdConfigAsistencia() {
        return idConfigAsistencia;
    }

    public void setIdConfigAsistencia(int idConfigAsistencia) {
        this.idConfigAsistencia = idConfigAsistencia;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}