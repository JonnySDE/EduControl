package com.educontrol.modelos;

public class ConfigCriteriosRequest {

    private int porcentajeTarea;
    private int porcentajeExamen;
    private int porcentajeParticipacion;
    private int porcentajeAsistencia;
    private int porcentajeDisciplina;

    public ConfigCriteriosRequest() {
    }

    public int getPorcentajeTarea() {
        return porcentajeTarea;
    }

    public void setPorcentajeTarea(int porcentajeTarea) {
        this.porcentajeTarea = porcentajeTarea;
    }

    public int getPorcentajeExamen() {
        return porcentajeExamen;
    }

    public void setPorcentajeExamen(int porcentajeExamen) {
        this.porcentajeExamen = porcentajeExamen;
    }

    public int getPorcentajeParticipacion() {
        return porcentajeParticipacion;
    }

    public void setPorcentajeParticipacion(int porcentajeParticipacion) {
        this.porcentajeParticipacion = porcentajeParticipacion;
    }

    public int getPorcentajeAsistencia() {
        return porcentajeAsistencia;
    }

    public void setPorcentajeAsistencia(int porcentajeAsistencia) {
        this.porcentajeAsistencia = porcentajeAsistencia;
    }

    public int getPorcentajeDisciplina() {
        return porcentajeDisciplina;
    }

    public void setPorcentajeDisciplina(int porcentajeDisciplina) {
        this.porcentajeDisciplina = porcentajeDisciplina;
    }
}