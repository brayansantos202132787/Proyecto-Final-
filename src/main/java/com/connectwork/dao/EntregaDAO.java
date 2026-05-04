/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.dao;

import com.connectwork.config.DBConnection;
import com.connectwork.model.Entrega;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author braya
 */
public class EntregaDAO {
    
    public int insert(Entrega e) throws SQLException {
        String sql = """
                     
            INSERT INTO entregas (id_contrato, descripcion, url_archivo, fecha_entrega, estado)
            VALUES (?, ?, ?, CURRENT_TIMESTAMP, 'PENDIENTE')
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, e.getIdContrato());
            ps.setString(2, e.getDescripcion());
            ps.setString(3, e.getUrlArchivo());
            ps.executeUpdate();
            try (ResultSet gen = ps.getGeneratedKeys()) {
                if (gen.next()) return gen.getInt(1);
            }
        }
        return -1;
    }

    public Entrega findById(int id) throws SQLException {
        String sql = "SELECT * FROM entregas WHERE id_entrega = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Entrega> findByContrato(int idContrato) throws SQLException {
        String sql = "SELECT * FROM entregas WHERE id_contrato = ? ORDER BY fecha_entrega DESC";
        List<Entrega> lista = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idContrato);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public boolean updateEstado(int idEntrega, String estado, String motivo) throws SQLException {
        String sql = "UPDATE entregas SET estado=?, motivo_rechazo=? WHERE id_entrega=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setString(2, motivo);
            ps.setInt   (3, idEntrega);
            return ps.executeUpdate() > 0;
        }
    }

    public int getContratoDeEntrega(int idEntrega) throws SQLException {
        String sql = "SELECT id_contrato FROM entregas WHERE id_entrega = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idEntrega);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id_contrato");
            }
        }
        throw new SQLException("Entrega no encontrada");
    }

    private Entrega mapRow(ResultSet rs) throws SQLException {
        Entrega e = new Entrega();
        e.setIdEntrega    (rs.getInt      ("id_entrega"));
        e.setIdContrato   (rs.getInt      ("id_contrato"));
        e.setDescripcion  (rs.getString   ("descripcion"));
        e.setUrlArchivo   (rs.getString   ("url_archivo"));
        e.setFechaEntrega (rs.getTimestamp("fecha_entrega"));
        e.setEstado       (rs.getString   ("estado"));
        e.setMotivoRechazo(rs.getString   ("motivo_rechazo"));
        return e;
    }
}
