package com.educontrol.dao;

import com.educontrol.Main;
import com.educontrol.modelos.Grupo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GrupoDAO {

    public List<Grupo> listarTodos() throws SQLException {
        List<Grupo> lista = new ArrayList<>();
        String sql = "SELECT * FROM grupo";

        try (Connection conn = Main.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearGrupo(rs));
            }
        }
        return lista;
    }

    public Grupo obtenerPorId(int idGrupo) throws SQLException {
        String sql = "SELECT * FROM grupo WHERE idGrupo = ?";

        try (Connection conn = Main.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idGrupo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearGrupo(rs);
            }
        }
        return null;
    }

    public int crear(Grupo grupo) throws SQLException {
        String sql = "INSERT INTO grupo (Grado, Grupo) VALUES (?, ?)";

        try (Connection conn = Main.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, grupo.getGrado());
            stmt.setString(2, grupo.getGrupo());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    public void actualizar(Grupo grupo) throws SQLException {
        String sql = "UPDATE grupo SET Grado = ?, Grupo = ? WHERE idGrupo = ?";

        try (Connection conn = Main.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, grupo.getGrado());
            stmt.setString(2, grupo.getGrupo());
            stmt.setInt(3, grupo.getIdGrupo());

            stmt.executeUpdate();
        }
    }

    public void eliminar(int idGrupo) throws SQLException {
        String sql = "DELETE FROM grupo WHERE idGrupo = ?";

        try (Connection conn = Main.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idGrupo);
            stmt.executeUpdate();
        }
    }

    private Grupo mapearGrupo(ResultSet rs) throws SQLException {
        return new Grupo(
            rs.getInt("idGrupo"),
            rs.getInt("Grado"),
            rs.getString("Grupo")
        );
    }
}