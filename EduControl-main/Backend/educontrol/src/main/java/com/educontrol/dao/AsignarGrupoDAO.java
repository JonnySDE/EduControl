package com.educontrol.dao;

import com.educontrol.Main;
import com.educontrol.modelos.AsignarGrupo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AsignarGrupoDAO {

    public List<AsignarGrupo> listarTodos() throws SQLException {
        List<AsignarGrupo> lista = new ArrayList<>();
        String sql = "SELECT * FROM asignar_grupo";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public AsignarGrupo obtenerPorUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT * FROM asignar_grupo WHERE idUsuario = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapear(rs);
            }
        }
        return null;
    }

    public void crear(AsignarGrupo asignacion) throws SQLException {
        String sql = "INSERT INTO asignar_grupo (idUsuario, idGrupo) VALUES (?, ?)";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, asignacion.getIdUsuario());
            stmt.setInt(2, asignacion.getIdGrupo());

            stmt.executeUpdate();
        }
    }

    public void actualizar(AsignarGrupo asignacion) throws SQLException {
        String sql = "UPDATE asignar_grupo SET idGrupo = ? WHERE idUsuario = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, asignacion.getIdGrupo());
            stmt.setInt(2, asignacion.getIdUsuario());

            stmt.executeUpdate();
        }
    }

    public void eliminar(int idUsuario) throws SQLException {
        String sql = "DELETE FROM asignar_grupo WHERE idUsuario = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            stmt.executeUpdate();
        }
    }

    private AsignarGrupo mapear(ResultSet rs) throws SQLException {
        return new AsignarGrupo(
            rs.getInt("idUsuario"),
            rs.getInt("idGrupo")
        );
    }
}