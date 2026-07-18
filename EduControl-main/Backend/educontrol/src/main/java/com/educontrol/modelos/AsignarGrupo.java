package com.educontrol.modelos;

public class AsignarGrupo {
    private int idUsuario;
    private int idGrupo;

    public AsignarGrupo() {
    }

    public AsignarGrupo(int idUsuario, int idGrupo) {
        this.idUsuario = idUsuario;
        this.idGrupo = idGrupo;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }
}