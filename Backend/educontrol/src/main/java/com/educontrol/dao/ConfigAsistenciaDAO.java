package com.educontrol.dao;

import com.educontrol.Main;
import com.educontrol.modelos.ConfigAsistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConfigAsistenciaDAO {

    public List<ConfigAsistencia> listarTodos() throws SQLException {
        List<ConfigAsistencia> lista = new ArrayList<>();
        String sql = "SELECT * FROM config_asistencia";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public ConfigAsistencia obtenerPorId(int idConfigAsistencia) throws SQLException {
        String sql = "SELECT * FROM config_asistencia WHERE idConfigAsistencia = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idConfigAsistencia);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapear(rs);
            }
        }
        return null;
    }

    public ConfigAsistencia obtenerPorUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT * FROM config_asistencia WHERE idUsuario = ?";

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

    public void crear(ConfigAsistencia config) throws SQLException {
        String sql = "INSERT INTO config_asistencia (porcentaje, idUsuario) VALUES (?, ?)";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, config.getPorcentaje());
            stmt.setInt(2, config.getIdUsuario());

            stmt.executeUpdate();
        }
    }

    public void actualizar(ConfigAsistencia config) throws SQLException {
        String sql = "UPDATE config_asistencia SET porcentaje = ? WHERE idUsuario = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, config.getPorcentaje());
            stmt.setInt(2, config.getIdUsuario());

            stmt.executeUpdate();
        }
    }

    public void eliminar(int idConfigAsistencia) throws SQLException {
        String sql = "DELETE FROM config_asistencia WHERE idConfigAsistencia = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idConfigAsistencia);
            stmt.executeUpdate();
        }
    }

    private ConfigAsistencia mapear(ResultSet rs) throws SQLException {
        return new ConfigAsistencia(
            rs.getInt("idConfigAsistencia"),
            rs.getInt("porcentaje"),
            rs.getInt("idUsuario")
        );
    }
}