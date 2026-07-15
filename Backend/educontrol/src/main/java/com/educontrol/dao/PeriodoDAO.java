package com.educontrol.dao;

import com.educontrol.Main;
import com.educontrol.modelos.Periodo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PeriodoDAO {

    public List<Periodo> listarTodos() throws SQLException {
        List<Periodo> lista = new ArrayList<>();
        String sql = "SELECT * FROM periodo";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Periodo obtenerPorId(int idPeriodo) throws SQLException {
        String sql = "SELECT * FROM periodo WHERE idPeriodo = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPeriodo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapear(rs);
            }
        }
        return null;
    }

    // Verifica si un periodo esta abierto (para bloquear registros en periodos cerrados)
    public boolean estaAbierto(int idPeriodo) throws SQLException {
        String sql = "SELECT estado FROM periodo WHERE idPeriodo = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPeriodo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return "Abierto".equals(rs.getString("estado"));
            }
        }
        return false;
    }

    public void crear(Periodo periodo) throws SQLException {
        String sql = "INSERT INTO periodo (estado, periodo, idGrupo, idCampoFormativo) VALUES (?, ?, ?, ?)";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, periodo.getEstado() != null ? periodo.getEstado() : "Abierto");
            stmt.setString(2, periodo.getPeriodo());
            stmt.setInt(3, periodo.getIdGrupo());
            stmt.setInt(4, periodo.getIdCampoFormativo());

            stmt.executeUpdate();
        }
    }

    public void actualizar(Periodo periodo) throws SQLException {
        String sql = "UPDATE periodo SET estado = ?, periodo = ?, idGrupo = ?, idCampoFormativo = ? WHERE idPeriodo = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, periodo.getEstado());
            stmt.setString(2, periodo.getPeriodo());
            stmt.setInt(3, periodo.getIdGrupo());
            stmt.setInt(4, periodo.getIdCampoFormativo());
            stmt.setInt(5, periodo.getIdPeriodo());

            stmt.executeUpdate();
        }
    }

    // Cierra el periodo: cambia estado a 'Cerrado' y registra la fecha/hora de cierre
    public void cerrarPeriodo(int idPeriodo) throws SQLException {
        String sql = "UPDATE periodo SET estado = 'Cerrado', fechaCierre = NOW() WHERE idPeriodo = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPeriodo);
            stmt.executeUpdate();
        }
    }

    public void eliminar(int idPeriodo) throws SQLException {
        String sql = "DELETE FROM periodo WHERE idPeriodo = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPeriodo);
            stmt.executeUpdate();
        }
    }

    private Periodo mapear(ResultSet rs) throws SQLException {
        Timestamp tsFechaCierre = rs.getTimestamp("fechaCierre");

        return new Periodo(
            rs.getInt("idPeriodo"),
            rs.getString("estado"),
            tsFechaCierre != null ? tsFechaCierre.toLocalDateTime() : null,
            rs.getString("periodo"),
            rs.getInt("idGrupo"),
            rs.getInt("idCampoFormativo")
        );
    }
}