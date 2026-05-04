/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.dao;


import com.connectwork.config.DBConnection;
import com.connectwork.model.Comision;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author braya
 */
public class ComisionDAO {
    public BigDecimal getPorcentajeActual() throws SQLException {
        String sql = "SELECT porcentaje FROM comisiones WHERE fecha_fin IS NULL ORDER BY fecha_inicio DESC LIMIT 1";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getBigDecimal("porcentaje");
        }
        return new BigDecimal("10.00");
    }

    public List<Comision> getHistorial() throws SQLException {
        String sql = "SELECT * FROM comisiones ORDER BY fecha_inicio DESC";
        List<Comision> lista = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    // Cierra la comisión activa y crea una nueva
    public void cambiarPorcentaje(BigDecimal nuevoPorcentaje) throws SQLException {
        String sqlCerrar = "UPDATE comisiones SET fecha_fin = CURRENT_TIMESTAMP WHERE fecha_fin IS NULL";
        String sqlInsertar = "INSERT INTO comisiones (porcentaje, fecha_inicio) VALUES (?, CURRENT_TIMESTAMP)";
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement ps1 = c.prepareStatement(sqlCerrar);
                 PreparedStatement ps2 = c.prepareStatement(sqlInsertar)) {
                ps1.executeUpdate();
                ps2.setBigDecimal(1, nuevoPorcentaje);
                ps2.executeUpdate();
                c.commit();
            } catch (SQLException e) {
                c.rollback(); throw e;
            }
        }
    }

    private Comision mapRow(ResultSet rs) throws SQLException {
        Comision com = new Comision();
        com.setIdComision (rs.getInt       ("id_comision"));
        com.setPorcentaje (rs.getBigDecimal("porcentaje"));
        com.setFechaInicio(rs.getTimestamp ("fecha_inicio"));
        com.setFechaFin   (rs.getTimestamp ("fecha_fin"));
        return com;
    }
}
