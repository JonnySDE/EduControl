package com.educontrol.dao;

import com.educontrol.Main;
import com.educontrol.modelos.RegistroAsistencia;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RegistroAsistenciaDAO {

    public List<RegistroAsistencia> listarTodos() throws SQLException {
        List<RegistroAsistencia> lista = new ArrayList<>();
        String sql = "SELECT * FROM registro_asistencia";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public RegistroAsistencia obtenerPorId(int idRegistroAsis) throws SQLException {
        String sql = "SELECT * FROM registro_asistencia WHERE idRegistroAsis = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idRegistroAsis);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapear(rs);
            }
        }
        return null;
    }

    public void crear(RegistroAsistencia registro) throws SQLException {
        String sql = "INSERT INTO registro_asistencia (estado, fecha, Matricula, idConfigAsistencia, idUsuario, idPeriodo) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, registro.getEstado());
            stmt.setDate(2, Date.valueOf(registro.getFecha()));
            stmt.setInt(3, registro.getMatricula());
            stmt.setInt(4, registro.getIdConfigAsistencia());
            stmt.setInt(5, registro.getIdUsuario());
            stmt.setInt(6, registro.getIdPeriodo());

            stmt.executeUpdate();
        }
    }

    public void actualizar(RegistroAsistencia registro) throws SQLException {
        String sql = "UPDATE registro_asistencia SET estado = ?, fecha = ?, Matricula = ?, " +
                     "idConfigAsistencia = ?, idUsuario = ?, idPeriodo = ? WHERE idRegistroAsis = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, registro.getEstado());
            stmt.setDate(2, Date.valueOf(registro.getFecha()));
            stmt.setInt(3, registro.getMatricula());
            stmt.setInt(4, registro.getIdConfigAsistencia());
            stmt.setInt(5, registro.getIdUsuario());
            stmt.setInt(6, registro.getIdPeriodo());
            stmt.setInt(7, registro.getIdRegistroAsis());

            stmt.executeUpdate();
        }
    }

    public void eliminar(int idRegistroAsis) throws SQLException {
        String sql = "DELETE FROM registro_asistencia WHERE idRegistroAsis = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idRegistroAsis);
            stmt.executeUpdate();
        }
    }

    public List<RegistroAsistencia> listarPorAlumnoPeriodo(int matricula, int idPeriodo) throws SQLException {
        List<RegistroAsistencia> lista = new ArrayList<>();
        String sql = "SELECT * FROM registro_asistencia WHERE Matricula = ? AND idPeriodo = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, matricula);
            stmt.setInt(2, idPeriodo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private RegistroAsistencia mapear(ResultSet rs) throws SQLException {
        Date fechaSql = rs.getDate("fecha");

        return new RegistroAsistencia(
            rs.getInt("idRegistroAsis"),
            rs.getString("estado"),
            fechaSql != null ? fechaSql.toLocalDate() : null,
            rs.getInt("Matricula"),
            rs.getInt("idConfigAsistencia"),
            rs.getInt("idUsuario"),
            rs.getInt("idPeriodo")
        );
    }
}