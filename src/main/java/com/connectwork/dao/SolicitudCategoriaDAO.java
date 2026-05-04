/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.dao;

import com.connectwork.config.DBConnection;
import com.connectwork.model.SolicitudCategoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author braya
 */
public class SolicitudCategoriaDAO {
     public int insert(SolicitudCategoria s) throws SQLException {
        String sql = """
            INSERT INTO solicitudes_categoria (id_cliente, nombre, descripcion, estado)
            VALUES (?,?,?,'PENDIENTE')
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, s.getIdCliente());
            ps.setString(2, s.getNombre());
            ps.setString(3, s.getDescripcion());
            ps.executeUpdate();
            try (ResultSet gen = ps.getGeneratedKeys()) {
                if (gen.next()) return gen.getInt(1);
            }
        }
        return -1;
    }

    public List<SolicitudCategoria> findAll() throws SQLException {
        String sql = """
            SELECT sc.*, u.nombre_completo AS nombre_cliente
            FROM solicitudes_categoria sc
            JOIN usuarios u ON sc.id_cliente = u.id_usuario
            ORDER BY sc.fecha DESC
            """;
        List<SolicitudCategoria> lista = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapRow(rs));
        }
        return lista;
    }

    public boolean updateEstado(int idSolicitud, String estado) throws SQLException {
        String sql = "UPDATE solicitudes_categoria SET estado=? WHERE id_solicitud=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, estado); ps.setInt(2, idSolicitud);
            return ps.executeUpdate() > 0;
        }
    }

    private SolicitudCategoria mapRow(ResultSet rs) throws SQLException {
        SolicitudCategoria s = new SolicitudCategoria();
        s.setIdSolicitud(rs.getInt      ("id_solicitud"));
        s.setIdCliente  (rs.getInt      ("id_cliente"));
        s.setNombre     (rs.getString   ("nombre"));
        s.setDescripcion(rs.getString   ("descripcion"));
        s.setEstado     (rs.getString   ("estado"));
        s.setFecha      (rs.getTimestamp("fecha"));
        try { s.setNombreCliente(rs.getString("nombre_cliente")); } catch (SQLException ignored) {}
        return s;
    }
}
