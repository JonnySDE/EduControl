package com.educontrol.dao;

import com.educontrol.Main;
import com.educontrol.modelos.AlumnoGrupo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AlumnoGrupoDAO {

    public List<AlumnoGrupo> listarTodos() throws SQLException {
        List<AlumnoGrupo> lista = new ArrayList<>();
        String sql = "SELECT * FROM alumno_grupo";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public AlumnoGrupo obtenerPorMatricula(int matricula) throws SQLException {
        String sql = "SELECT * FROM alumno_grupo WHERE Matricula = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, matricula);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapear(rs);
            }
        }
        return null;
    }

    public List<AlumnoGrupo> listarPorGrupo(int idGrupo) throws SQLException {
        List<AlumnoGrupo> lista = new ArrayList<>();
        String sql = "SELECT * FROM alumno_grupo WHERE idGrupo = ? ORDER BY NumeroLista";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idGrupo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public void crear(AlumnoGrupo ag) throws SQLException {
        String sql = "INSERT INTO alumno_grupo (Matricula, NumeroLista, idGrupo) VALUES (?, ?, ?)";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ag.getMatricula());
            stmt.setInt(2, ag.getNumeroLista());
            stmt.setInt(3, ag.getIdGrupo());

            stmt.executeUpdate();
        }
    }

    // Cambia al alumno de grupo (promoción de grado o reasignación) - actualiza la fila existente
    public void cambiarGrupo(int matricula, int nuevoIdGrupo, int nuevoNumeroLista) throws SQLException {
        String sql = "UPDATE alumno_grupo SET idGrupo = ?, NumeroLista = ? WHERE Matricula = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, nuevoIdGrupo);
            stmt.setInt(2, nuevoNumeroLista);
            stmt.setInt(3, matricula);

            stmt.executeUpdate();
        }
    }

    public void eliminar(int matricula) throws SQLException {
        String sql = "DELETE FROM alumno_grupo WHERE Matricula = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, matricula);
            stmt.executeUpdate();
        }
    }

    private AlumnoGrupo mapear(ResultSet rs) throws SQLException {
        return new AlumnoGrupo(
            rs.getInt("idAG"),
            rs.getInt("Matricula"),
            rs.getInt("NumeroLista"),
            rs.getInt("idGrupo")
        );
    }
}