package com.educontrol.dao;

import com.educontrol.Main;
import com.educontrol.modelos.Promedio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PromedioDAO {

    public List<Promedio> listarTodos() throws SQLException {
        List<Promedio> lista = new ArrayList<>();
        String sql = "SELECT * FROM promedio";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Promedio obtenerPorId(int idPromedio) throws SQLException {
        String sql = "SELECT * FROM promedio WHERE idPromedio = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPromedio);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapear(rs);
            }
        }
        return null;
    }

    public Promedio obtenerPorMatriculaCampoPeriodo(int matricula, int idCampoFormativo, int idPeriodo) throws SQLException {
        String sql = "SELECT * FROM promedio WHERE Matricula = ? AND idCampoFormativo = ? AND idPeriodo = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, matricula);
            stmt.setInt(2, idCampoFormativo);
            stmt.setInt(3, idPeriodo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapear(rs);
            }
        }
        return null;
    }

    public void crear(Promedio promedio) throws SQLException {
        String sql = "INSERT INTO promedio (Matricula, idCampoFormativo, PromedioFinal, Grado, idPeriodo) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, promedio.getMatricula());
            stmt.setInt(2, promedio.getIdCampoFormativo());
            stmt.setBigDecimal(3, promedio.getPromedioFinal());
            stmt.setInt(4, promedio.getGrado());
            stmt.setInt(5, promedio.getIdPeriodo());

            stmt.executeUpdate();
        }
    }

    public void actualizar(Promedio promedio) throws SQLException {
        String sql = "UPDATE promedio SET Matricula = ?, idCampoFormativo = ?, PromedioFinal = ?, " +
                     "Grado = ?, idPeriodo = ? WHERE idPromedio = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, promedio.getMatricula());
            stmt.setInt(2, promedio.getIdCampoFormativo());
            stmt.setBigDecimal(3, promedio.getPromedioFinal());
            stmt.setInt(4, promedio.getGrado());
            stmt.setInt(5, promedio.getIdPeriodo());
            stmt.setInt(6, promedio.getIdPromedio());

            stmt.executeUpdate();
        }
    }

    public void eliminar(int idPromedio) throws SQLException {
        String sql = "DELETE FROM promedio WHERE idPromedio = ?";

        try (Connection conn = Main.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPromedio);
            stmt.executeUpdate();
        }
    }

    private Promedio mapear(ResultSet rs) throws SQLException {
        return new Promedio(
            rs.getInt("idPromedio"),
            rs.getInt("Matricula"),
            rs.getInt("idCampoFormativo"),
            rs.getBigDecimal("PromedioFinal"),
            rs.getInt("Grado"),
            rs.getInt("idPeriodo")
        );
    }
}