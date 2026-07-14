package com.educontrol.dao;

import com.educontrol.Main;
import com.educontrol.modelos.RegistroExamen;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RegistroExamenDAO {

    public List<RegistroExamen> listarTodos() throws SQLException {
        List<RegistroExamen> lista = new ArrayList<>();
        String sql = "SELECT * FROM registro_examen";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public RegistroExamen obtenerPorId(int idRegistroExamen) throws SQLException {
        String sql = "SELECT * FROM registro_examen WHERE idRegistroExamen = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idRegistroExamen);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapear(rs);
            }
        }
        return null;
    }

    public void crear(RegistroExamen registro) throws SQLException {
        String sql = "INSERT INTO registro_examen (fecha, NombreExamen, Matricula, idExamen, idUsuario, idCampoFormativo, idPeriodo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(registro.getFecha()));
            stmt.setString(2, registro.getNombreExamen());
            stmt.setInt(3, registro.getMatricula());
            stmt.setInt(4, registro.getIdExamen());
            stmt.setInt(5, registro.getIdUsuario());
            stmt.setInt(6, registro.getIdCampoFormativo());
            stmt.setInt(7, registro.getIdPeriodo());

            stmt.executeUpdate();
        }
    }

    public void actualizar(RegistroExamen registro) throws SQLException {
        String sql = "UPDATE registro_examen SET fecha = ?, NombreExamen = ?, Matricula = ?, " +
                     "idExamen = ?, idUsuario = ?, idCampoFormativo = ?, idPeriodo = ? WHERE idRegistroExamen = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(registro.getFecha()));
            stmt.setString(2, registro.getNombreExamen());
            stmt.setInt(3, registro.getMatricula());
            stmt.setInt(4, registro.getIdExamen());
            stmt.setInt(5, registro.getIdUsuario());
            stmt.setInt(6, registro.getIdCampoFormativo());
            stmt.setInt(7, registro.getIdPeriodo());
            stmt.setInt(8, registro.getIdRegistroExamen());

            stmt.executeUpdate();
        }
    }

    public void eliminar(int idRegistroExamen) throws SQLException {
        String sql = "DELETE FROM registro_examen WHERE idRegistroExamen = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idRegistroExamen);
            stmt.executeUpdate();
        }
    }

    public List<RegistroExamen> listarPorAlumnoCampoPeriodo(int matricula, int idCampoFormativo, int idPeriodo) throws SQLException {
        List<RegistroExamen> lista = new ArrayList<>();
        String sql = "SELECT * FROM registro_examen WHERE Matricula = ? AND idCampoFormativo = ? AND idPeriodo = ?";

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
    
    private RegistroExamen mapear(ResultSet rs) throws SQLException {
        Date fechaSql = rs.getDate("fecha");

        return new RegistroExamen(
            rs.getInt("idRegistroExamen"),
            fechaSql != null ? fechaSql.toLocalDate() : null,
            rs.getString("NombreExamen"),
            rs.getInt("Matricula"),
            rs.getInt("idExamen"),
            rs.getInt("idUsuario"),
            rs.getInt("idCampoFormativo"),
            rs.getInt("idPeriodo")
        );
    }
}