package com.educontrol.dao;

import com.educontrol.Main;
import com.educontrol.modelos.ConfigDisciplina;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConfigDisciplinaDAO {

    public List<ConfigDisciplina> listarTodos() throws SQLException {
        List<ConfigDisciplina> lista = new ArrayList<>();
        String sql = "SELECT * FROM config_disciplina";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public ConfigDisciplina obtenerPorId(int idConfigDisciplina) throws SQLException {
        String sql = "SELECT * FROM config_disciplina WHERE idConfigDisciplina = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idConfigDisciplina);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapear(rs);
            }
        }
        return null;
    }

    public ConfigDisciplina obtenerPorUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT * FROM config_disciplina WHERE idUsuario = ?";

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

    public void crear(ConfigDisciplina config) throws SQLException {
        String sql = "INSERT INTO config_disciplina (porcentaje, idUsuario) VALUES (?, ?)";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, config.getPorcentaje());
            stmt.setInt(2, config.getIdUsuario());

            stmt.executeUpdate();
        }
    }

    public void actualizar(ConfigDisciplina config) throws SQLException {
        String sql = "UPDATE config_disciplina SET porcentaje = ? WHERE idUsuario = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, config.getPorcentaje());
            stmt.setInt(2, config.getIdUsuario());

            stmt.executeUpdate();
        }
    }

    public void eliminar(int idConfigDisciplina) throws SQLException {
        String sql = "DELETE FROM config_disciplina WHERE idConfigDisciplina = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idConfigDisciplina);
            stmt.executeUpdate();
        }
    }

    private ConfigDisciplina mapear(ResultSet rs) throws SQLException {
        return new ConfigDisciplina(
            rs.getInt("idConfigDisciplina"),
            rs.getInt("porcentaje"),
            rs.getInt("idUsuario")
        );
    }
}