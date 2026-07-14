package com.educontrol.modelos;

public class AlumnoGrupo {
    private int idAG;
    private int matricula;
    private int numeroLista;
    private int idGrupo;

    public AlumnoGrupo() {
    }

    public AlumnoGrupo(int idAG, int matricula, int numeroLista, int idGrupo) {
        this.idAG = idAG;
        this.matricula = matricula;
        this.numeroLista = numeroLista;
        this.idGrupo = idGrupo;
    }

    public int getIdAG() {
        return idAG;
    }

    public void setIdAG(int idAG) {
        this.idAG = idAG;
    }

    public int getMatricula() {
        return matricula;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public int getNumeroLista() {
        return numeroLista;
    }

    public void setNumeroLista(int numeroLista) {
        this.numeroLista = numeroLista;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }
}