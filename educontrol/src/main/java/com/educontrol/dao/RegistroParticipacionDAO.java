package com.educontrol.dao;

import com.educontrol.Main;
import com.educontrol.modelos.RegistroParticipacion;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RegistroParticipacionDAO {

    public List<RegistroParticipacion> listarTodos() throws SQLException {
        List<RegistroParticipacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM registro_participacion";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public RegistroParticipacion obtenerPorId(int idRegistroParticipacion) throws SQLException {
        String sql = "SELECT * FROM registro_participacion WHERE idRegistroParticipacion = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idRegistroParticipacion);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapear(rs);
            }
        }
        return null;
    }

    public List<RegistroParticipacion> listarPorAlumnoCampoPeriodo(int matricula, int idCampoFormativo, int idPeriodo) throws SQLException {
        List<RegistroParticipacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM registro_participacion WHERE Matricula = ? AND idCampoFormativo = ? AND idPeriodo = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, matricula);
            stmt.setInt(2, idCampoFormativo);
            stmt.setInt(3, idPeriodo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public void crear(RegistroParticipacion registro) throws SQLException {
        String sql = "INSERT INTO registro_participacion (Puntuacion, fecha, Matricula, idParticipacion, idUsuario, idCampoFormativo, idPeriodo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setFloat(1, registro.getPuntuacion());
            stmt.setDate(2, Date.valueOf(registro.getFecha()));
            stmt.setInt(3, registro.getMatricula());
            stmt.setInt(4, registro.getIdParticipacion());
            stmt.setInt(5, registro.getIdUsuario());
            stmt.setInt(6, registro.getIdCampoFormativo());
            stmt.setInt(7, registro.getIdPeriodo());

            stmt.executeUpdate();
        }
    }

    public void actualizar(RegistroParticipacion registro) throws SQLException {
        String sql = "UPDATE registro_participacion SET Puntuacion = ?, fecha = ?, Matricula = ?, " +
                     "idParticipacion = ?, idUsuario = ?, idCampoFormativo = ?, idPeriodo = ? WHERE idRegistroParticipacion = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setFloat(1, registro.getPuntuacion());
            stmt.setDate(2, Date.valueOf(registro.getFecha()));
            stmt.setInt(3, registro.getMatricula());
            stmt.setInt(4, registro.getIdParticipacion());
            stmt.setInt(5, registro.getIdUsuario());
            stmt.setInt(6, registro.getIdCampoFormativo());
            stmt.setInt(7, registro.getIdPeriodo());
            stmt.setInt(8, registro.getIdRegistroParticipacion());

            stmt.executeUpdate();
        }
    }

    public void eliminar(int idRegistroParticipacion) throws SQLException {
        String sql = "DELETE FROM registro_participacion WHERE idRegistroParticipacion = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idRegistroParticipacion);
            stmt.executeUpdate();
        }
    }

    private RegistroParticipacion mapear(ResultSet rs) throws SQLException {
        Date fechaSql = rs.getDate("fecha");

        return new RegistroParticipacion(
            rs.getInt("idRegistroParticipacion"),
            rs.getFloat("Puntuacion"),
            fechaSql != null ? fechaSql.toLocalDate() : null,
            rs.getInt("Matricula"),
            rs.getInt("idParticipacion"),
            rs.getInt("idUsuario"),
            rs.getInt("idCampoFormativo"),
            rs.getInt("idPeriodo")
        );
    }
}