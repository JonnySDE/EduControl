package com.educontrol.dao;

import com.educontrol.Main;
import com.educontrol.modelos.ConfigParticipacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConfigParticipacionDAO {

    public List<ConfigParticipacion> listarTodos() throws SQLException {
        List<ConfigParticipacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM config_participacion";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public ConfigParticipacion obtenerPorId(int idParticipacion) throws SQLException {
        String sql = "SELECT * FROM config_participacion WHERE idParticipacion = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idParticipacion);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapear(rs);
            }
        }
        return null;
    }

    public void crear(ConfigParticipacion config) throws SQLException {
        String sql = "INSERT INTO config_participacion (porcentaje, idUsuario, idCampoFormativo) VALUES (?, ?, ?)";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, config.getPorcentaje());
            stmt.setInt(2, config.getIdUsuario());
            stmt.setInt(3, config.getIdCampoFormativo());

            stmt.executeUpdate();
        }
    }

    public void actualizar(ConfigParticipacion config) throws SQLException {
        String sql = "UPDATE config_participacion SET porcentaje = ?, idUsuario = ?, idCampoFormativo = ? WHERE idParticipacion = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, config.getPorcentaje());
            stmt.setInt(2, config.getIdUsuario());
            stmt.setInt(3, config.getIdCampoFormativo());
            stmt.setInt(4, config.getIdParticipacion());

            stmt.executeUpdate();
        }
    }

    public void eliminar(int idParticipacion) throws SQLException {
        String sql = "DELETE FROM config_participacion WHERE idParticipacion = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idParticipacion);
            stmt.executeUpdate();
        }
    }

    private ConfigParticipacion mapear(ResultSet rs) throws SQLException {
        return new ConfigParticipacion(
            rs.getInt("idParticipacion"),
            rs.getInt("porcentaje"),
            rs.getInt("idUsuario"),
            rs.getInt("idCampoFormativo")
        );
    }
}