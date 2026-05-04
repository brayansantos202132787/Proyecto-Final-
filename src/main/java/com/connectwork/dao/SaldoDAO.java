/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.dao;

import com.connectwork.config.DBConnection;
import com.connectwork.model.RecargaSaldo;
import com.connectwork.model.Saldo;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author braya
 */
public class SaldoDAO {
     public void initSaldo(int idUsuario) throws SQLException {
        String sql = "INSERT INTO saldos (id_usuario, saldo_disponible, saldo_bloqueado) VALUES (?,0.00,0.00)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUsuario); ps.executeUpdate();
        }
    }

    public BigDecimal getSaldo(int idUsuario) throws SQLException {
        String sql = "SELECT saldo_disponible FROM saldos WHERE id_usuario = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal("saldo_disponible");
            }
        }
        return BigDecimal.ZERO;
    }

    public Saldo getSaldoCompleto(int idUsuario) throws SQLException {
        String sql = "SELECT * FROM saldos WHERE id_usuario = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Saldo s = new Saldo();
                    s.setIdUsuario      (rs.getInt       ("id_usuario"));
                    s.setSaldoDisponible(rs.getBigDecimal("saldo_disponible"));
                    s.setSaldoBloqueado (rs.getBigDecimal("saldo_bloqueado"));
                    return s;
                }
            }
        }
        return null;
    }

    public void recargar(int idUsuario, BigDecimal monto) throws SQLException {
        String sqlSaldo   = "UPDATE saldos SET saldo_disponible = saldo_disponible + ? WHERE id_usuario = ?";
        String sqlHistorial = "INSERT INTO recargas_saldo (id_usuario, monto) VALUES (?,?)";
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement ps1 = c.prepareStatement(sqlSaldo);
                 PreparedStatement ps2 = c.prepareStatement(sqlHistorial)) {
                ps1.setBigDecimal(1, monto); ps1.setInt(2, idUsuario); ps1.executeUpdate();
                ps2.setInt(1, idUsuario); ps2.setBigDecimal(2, monto); ps2.executeUpdate();
                c.commit();
            } catch (SQLException e) {
                c.rollback(); throw e;
            }
        }
    }

    public void bloquearMonto(int idUsuario, BigDecimal monto) throws SQLException {
        String sql = """
            UPDATE saldos
            SET saldo_disponible = saldo_disponible - ?,
                saldo_bloqueado  = saldo_bloqueado  + ?
            WHERE id_usuario = ? AND saldo_disponible >= ?
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBigDecimal(1, monto); ps.setBigDecimal(2, monto);
            ps.setInt(3, idUsuario);   ps.setBigDecimal(4, monto);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new IllegalStateException("Saldo insuficiente");
        }
    }

    public void devolverMonto(int idUsuario, BigDecimal monto) throws SQLException {
        String sql = """
            UPDATE saldos
            SET saldo_disponible = saldo_disponible + ?,
                saldo_bloqueado  = saldo_bloqueado  - ?
            WHERE id_usuario = ?
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBigDecimal(1, monto); ps.setBigDecimal(2, monto); ps.setInt(3, idUsuario);
            ps.executeUpdate();
        }
    }

    public void acreditar(int idUsuario, BigDecimal monto) throws SQLException {
        String sql = "UPDATE saldos SET saldo_disponible = saldo_disponible + ? WHERE id_usuario = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBigDecimal(1, monto); ps.setInt(2, idUsuario); ps.executeUpdate();
        }
    }

    public void desbloquear(int idUsuario, BigDecimal monto) throws SQLException {
        String sql = "UPDATE saldos SET saldo_bloqueado = saldo_bloqueado - ? WHERE id_usuario = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBigDecimal(1, monto); ps.setInt(2, idUsuario); ps.executeUpdate();
        }
    }

    public void acumularComisionGlobal(BigDecimal monto) throws SQLException {
        String sql = "UPDATE saldo_plataforma SET total = total + ? WHERE id = 1";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBigDecimal(1, monto); ps.executeUpdate();
        }
    }

    public BigDecimal getSaldoGlobal() throws SQLException {
        String sql = "SELECT total FROM saldo_plataforma WHERE id = 1";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getBigDecimal("total");
        }
        return BigDecimal.ZERO;
    }

    public List<RecargaSaldo> getHistorialRecargas(int idUsuario) throws SQLException {
        String sql = "SELECT * FROM recargas_saldo WHERE id_usuario = ? ORDER BY fecha_recarga DESC";
        List<RecargaSaldo> lista = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RecargaSaldo r = new RecargaSaldo();
                    r.setIdRecarga  (rs.getInt       ("id_recarga"));
                    r.setIdUsuario  (rs.getInt       ("id_usuario"));
                    r.setMonto      (rs.getBigDecimal("monto"));
                    r.setFechaRecarga(rs.getTimestamp("fecha_recarga"));
                    lista.add(r);
                }
            }
        }
        return lista;
    }
}
