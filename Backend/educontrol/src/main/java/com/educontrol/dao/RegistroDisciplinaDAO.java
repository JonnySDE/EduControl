package com.educontrol.dao;

import com.educontrol.Main;
import com.educontrol.modelos.RegistroDisciplina;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RegistroDisciplinaDAO {

    public List<RegistroDisciplina> listarTodos() throws SQLException {
        List<RegistroDisciplina> lista = new ArrayList<>();
        String sql = "SELECT * FROM registro_disciplina";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public RegistroDisciplina obtenerPorId(int idRegistroDisciplina) throws SQLException {
        String sql = "SELECT * FROM registro_disciplina WHERE idRegistroDisciplina = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idRegistroDisciplina);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapear(rs);
            }
        }
        return null;
    }

    // Necesario para el calculo del promedio: suma todos los puntos menos de un alumno en un periodo
    public int sumarPuntosMenosPorAlumnoPeriodo(int matricula, int idPeriodo) throws SQLException {
        String sql = "SELECT COALESCE(SUM(PuntosMenos), 0) as total FROM registro_disciplina WHERE Matricula = ? AND idPeriodo = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, matricula);
            stmt.setInt(2, idPeriodo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    public void crear(RegistroDisciplina registro) throws SQLException {
        String sql = "INSERT INTO registro_disciplina (Observaciones, PuntosMenos, fecha, Matricula, idConfigDisciplina, idUsuario, idPeriodo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, registro.getObservaciones());
            stmt.setInt(2, registro.getPuntosMenos());
            stmt.setDate(3, Date.valueOf(registro.getFecha()));
            stmt.setInt(4, registro.getMatricula());
            stmt.setInt(5, registro.getIdConfigDisciplina());
            stmt.setInt(6, registro.getIdUsuario());
            stmt.setInt(7, registro.getIdPeriodo());

            stmt.executeUpdate();
        }
    }

    public void actualizar(RegistroDisciplina registro) throws SQLException {
        String sql = "UPDATE registro_disciplina SET Observaciones = ?, PuntosMenos = ?, fecha = ?, " +
                     "Matricula = ?, idConfigDisciplina = ?, idUsuario = ?, idPeriodo = ? WHERE idRegistroDisciplina = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, registro.getObservaciones());
            stmt.setInt(2, registro.getPuntosMenos());
            stmt.setDate(3, Date.valueOf(registro.getFecha()));
            stmt.setInt(4, registro.getMatricula());
            stmt.setInt(5, registro.getIdConfigDisciplina());
            stmt.setInt(6, registro.getIdUsuario());
            stmt.setInt(7, registro.getIdPeriodo());
            stmt.setInt(8, registro.getIdRegistroDisciplina());

            stmt.executeUpdate();
        }
    }

    public void eliminar(int idRegistroDisciplina) throws SQLException {
        String sql = "DELETE FROM registro_disciplina WHERE idRegistroDisciplina = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idRegistroDisciplina);
            stmt.executeUpdate();
        }
    }

    private RegistroDisciplina mapear(ResultSet rs) throws SQLException {
        Date fechaSql = rs.getDate("fecha");

        return new RegistroDisciplina(
            rs.getInt("idRegistroDisciplina"),
            rs.getString("Observaciones"),
            rs.getInt("PuntosMenos"),
            fechaSql != null ? fechaSql.toLocalDate() : null,
            rs.getInt("Matricula"),
            rs.getInt("idConfigDisciplina"),
            rs.getInt("idUsuario"),
            rs.getInt("idPeriodo")
        );
    }
}