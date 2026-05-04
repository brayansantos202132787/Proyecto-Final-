/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.dao;

import com.connectwork.config.DBConnection;
import com.connectwork.model.SolicitudHabilidad;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author braya
 */
public class SolicitudHabilidadDAO {
     public int insert(SolicitudHabilidad s) throws SQLException {
        String sql = """
            INSERT INTO solicitudes_habilidad (id_freelancer, nombre, descripcion, estado)
            VALUES (?,?,?,'PENDIENTE')
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, s.getIdFreelancer());
            ps.setString(2, s.getNombre());
            ps.setString(3, s.getDescripcion());
            ps.executeUpdate();
            try (ResultSet gen = ps.getGeneratedKeys()) {
                if (gen.next()) return gen.getInt(1);
            }
        }
        return -1;
    }

    public List<SolicitudHabilidad> findAll() throws SQLException {
        String sql = """
            SELECT sh.*, u.nombre_completo AS nombre_freelancer
            FROM solicitudes_habilidad sh
            JOIN usuarios u ON sh.id_freelancer = u.id_usuario
            ORDER BY sh.fecha DESC
            """;
        List<SolicitudHabilidad> lista = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    public List<SolicitudHabilidad> findByFreelancer(int idFreelancer) throws SQLException {
        String sql = """
            SELECT sh.*, u.nombre_completo AS nombre_freelancer
            FROM solicitudes_habilidad sh
            JOIN usuarios u ON sh.id_freelancer = u.id_usuario
            WHERE sh.id_freelancer = ?
            """;
        List<SolicitudHabilidad> lista = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idFreelancer);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public boolean updateEstado(int idSolicitud, String estado) throws SQLException {
        String sql = "UPDATE solicitudes_habilidad SET estado=? WHERE id_solicitud=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, estado); ps.setInt(2, idSolicitud);
            return ps.executeUpdate() > 0;
        }
    }

    private SolicitudHabilidad mapRow(ResultSet rs) throws SQLException {
        SolicitudHabilidad s = new SolicitudHabilidad();
        s.setIdSolicitud  (rs.getInt      ("id_solicitud"));
        s.setIdFreelancer (rs.getInt      ("id_freelancer"));
        s.setNombre       (rs.getString   ("nombre"));
        s.setDescripcion  (rs.getString   ("descripcion"));
        s.setEstado       (rs.getString   ("estado"));
        s.setFecha        (rs.getTimestamp("fecha"));
        try { s.setNombreFreelancer(rs.getString("nombre_freelancer")); } catch (SQLException ignored) {}
        return s;
    }
}
