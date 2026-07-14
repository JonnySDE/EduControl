package com.educontrol.modelos;

public class DetalleExamen {
    private int idDetalleExamen;
    private int idRegistroExamen;
    private int totalPreguntas;
    private float aciertos;

    public DetalleExamen() {
    }

    public DetalleExamen(int idDetalleExamen, int idRegistroExamen, int totalPreguntas, float aciertos) {
        this.idDetalleExamen = idDetalleExamen;
        this.idRegistroExamen = idRegistroExamen;
        this.totalPreguntas = totalPreguntas;
        this.aciertos = aciertos;
    }

    public int getIdDetalleExamen() {
        return idDetalleExamen;
    }

    public void setIdDetalleExamen(int idDetalleExamen) {
        this.idDetalleExamen = idDetalleExamen;
    }

    public int getIdRegistroExamen() {
        return idRegistroExamen;
    }

    public void setIdRegistroExamen(int idRegistroExamen) {
        this.idRegistroExamen = idRegistroExamen;
    }

    public int getTotalPreguntas() {
        return totalPreguntas;
    }

    public void setTotalPreguntas(int totalPreguntas) {
        this.totalPreguntas = totalPreguntas;
    }

    public float getAciertos() {
        return aciertos;
    }

    public void setAciertos(float aciertos) {
        this.aciertos = aciertos;
    }
}