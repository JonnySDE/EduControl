package com.educontrol.dao;

import com.educontrol.Main;
import com.educontrol.modelos.ConfigExamen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConfigExamenDAO {

    public List<ConfigExamen> listarTodos() throws SQLException {
        List<ConfigExamen> lista = new ArrayList<>();
        String sql = "SELECT * FROM config_examen";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public ConfigExamen obtenerPorId(int idExamen) throws SQLException {
        String sql = "SELECT * FROM config_examen WHERE idExamen = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idExamen);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapear(rs);
            }
        }
        return null;
    }

    public void crear(ConfigExamen config) throws SQLException {
        String sql = "INSERT INTO config_examen (porcentaje, idUsuario) VALUES (?, ?)";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, config.getPorcentaje());
            stmt.setInt(2, config.getIdUsuario());

            stmt.executeUpdate();
        }
    }

    public void actualizar(ConfigExamen config) throws SQLException {
        String sql = "UPDATE config_examen SET porcentaje = ?, idUsuario = ? WHERE idExamen = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, config.getPorcentaje());
            stmt.setInt(2, config.getIdUsuario());
            stmt.setInt(3, config.getIdExamen());

            stmt.executeUpdate();
        }
    }

    public void eliminar(int idExamen) throws SQLException {
        String sql = "DELETE FROM config_examen WHERE idExamen = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idExamen);
            stmt.executeUpdate();
        }
    }

    private ConfigExamen mapear(ResultSet rs) throws SQLException {
        return new ConfigExamen(
            rs.getInt("idExamen"),
            rs.getInt("porcentaje"),
            rs.getInt("idUsuario")
        );
    }
}