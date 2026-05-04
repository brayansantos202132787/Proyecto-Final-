/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.dao;

import com.connectwork.config.DBConnection;
import com.connectwork.model.Calificacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author braya
 */
public class CalificacionDAO {
    public boolean insert(Calificacion cal) throws SQLException {
        String sql = """
            INSERT INTO calificaciones (id_contrato, estrellas, comentario, fecha)
            VALUES (?, ?, ?, CURRENT_TIMESTAMP)
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt   (1, cal.getIdContrato());
            ps.setInt   (2, cal.getEstrellas());
            ps.setString(3, cal.getComentario());
            return ps.executeUpdate() > 0;
        }
    }

    public double getPromedioFreelancer(int idFreelancer) throws SQLException {
        String sql = """
            SELECT COALESCE(AVG(cal.estrellas), 0) AS promedio
            FROM calificaciones cal
            JOIN contratos ct    ON cal.id_contrato  = ct.id_contrato
            JOIN propuestas p    ON ct.id_propuesta  = p.id_propuesta
            WHERE p.id_freelancer = ?
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idFreelancer);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("promedio");
            }
        }
        return 0.0;
    }

    public List<Calificacion> findByFreelancer(int idFreelancer) throws SQLException {
        String sql = """
            SELECT cal.*, uc.nombre_completo AS nombre_cliente, pr.titulo AS titulo_proyecto
            FROM calificaciones cal
            JOIN contratos ct  ON cal.id_contrato = ct.id_contrato
            JOIN propuestas p  ON ct.id_propuesta = p.id_propuesta
            JOIN proyectos pr  ON p.id_proyecto   = pr.id_proyecto
            JOIN usuarios uc   ON pr.id_cliente   = uc.id_usuario
            WHERE p.id_freelancer = ?
            ORDER BY cal.fecha DESC
            """;
        List<Calificacion> lista = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idFreelancer);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Calificacion cal = new Calificacion();
                    cal.setIdCalificacion(rs.getInt    ("id_calificacion"));
                    cal.setIdContrato    (rs.getInt    ("id_contrato"));
                    cal.setEstrellas     (rs.getInt    ("estrellas"));
                    cal.setComentario    (rs.getString ("comentario"));
                    cal.setFecha         (rs.getTimestamp("fecha"));
                    cal.setNombreCliente (rs.getString ("nombre_cliente"));
                    cal.setTituloProyecto(rs.getString ("titulo_proyecto"));
                    lista.add(cal);
                }
            }
        }
        return lista;
    }

    public boolean existeParaContrato(int idContrato) throws SQLException {
        String sql = "SELECT 1 FROM calificaciones WHERE id_contrato = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idContrato);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }
}
