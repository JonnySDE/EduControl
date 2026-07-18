package com.educontrol.dao;

import com.educontrol.Main;
import com.educontrol.modelos.ConfigTarea;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConfigTareaDAO {

    public List<ConfigTarea> listarTodos() throws SQLException {
        List<ConfigTarea> lista = new ArrayList<>();
        String sql = "SELECT * FROM config_tarea";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public ConfigTarea obtenerPorId(int idTarea) throws SQLException {
        String sql = "SELECT * FROM config_tarea WHERE idTarea = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idTarea);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapear(rs);
            }
        }
        return null;
    }

    public ConfigTarea obtenerPorUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT * FROM config_tarea WHERE idUsuario = ?";

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

    public void crear(ConfigTarea config) throws SQLException {
        String sql = "INSERT INTO config_tarea (porcentaje, idUsuario) VALUES (?, ?)";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, config.getPorcentaje());
            stmt.setInt(2, config.getIdUsuario());

            stmt.executeUpdate();
        }
    }

    public void actualizar(ConfigTarea config) throws SQLException {
        String sql = "UPDATE config_tarea SET porcentaje = ? WHERE idUsuario = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, config.getPorcentaje());
            stmt.setInt(2, config.getIdUsuario());

            stmt.executeUpdate();
        }
    }

    public void eliminar(int idTarea) throws SQLException {
        String sql = "DELETE FROM config_tarea WHERE idTarea = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idTarea);
            stmt.executeUpdate();
        }
    }

    private ConfigTarea mapear(ResultSet rs) throws SQLException {
        return new ConfigTarea(
            rs.getInt("idTarea"),
            rs.getInt("porcentaje"),
            rs.getInt("idUsuario")
        );
    }
}