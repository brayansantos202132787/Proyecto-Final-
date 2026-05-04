/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.dao;

import com.connectwork.config.DBConnection;
import com.connectwork.model.Propuesta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author braya
 */
public class PropuestaDAO {
    public int insert(Propuesta p) throws SQLException {
        String sql = """
            INSERT INTO propuestas
              (id_proyecto, id_freelancer, monto_ofertado, plazo_entrega_dias, carta_presentacion, estado)
            VALUES (?,?,?,?,?,'PENDIENTE')
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt       (1, p.getIdProyecto());
            ps.setInt       (2, p.getIdFreelancer());
            ps.setBigDecimal(3, p.getMontoOfertado());
            ps.setInt       (4, p.getPlazoEntregaDias());
            ps.setString    (5, p.getCartaPresentacion());
            ps.executeUpdate();
            try (ResultSet gen = ps.getGeneratedKeys()) {
                if (gen.next()) return gen.getInt(1);
            }
        }
        return -1;
    }

    public Propuesta findById(int id) throws SQLException {
        String sql = """
            SELECT p.*, u.nombre_completo AS nombre_freelancer,
                   pr.titulo AS titulo_proyecto,
                   COALESCE(AVG(cal.estrellas), 0) AS calificacion_promedio
            FROM propuestas p
            JOIN usuarios u  ON p.id_freelancer = u.id_usuario
            JOIN proyectos pr ON p.id_proyecto  = pr.id_proyecto
            LEFT JOIN contratos ct  ON ct.id_propuesta  = p.id_propuesta
            LEFT JOIN calificaciones cal ON cal.id_contrato = ct.id_contrato
            WHERE p.id_propuesta = ?
            GROUP BY p.id_propuesta
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Propuesta> findByProyecto(int idProyecto) throws SQLException {
        String sql = """
            SELECT p.*, u.nombre_completo AS nombre_freelancer,
                   pr.titulo AS titulo_proyecto,
                   COALESCE(AVG(cal.estrellas), 0) AS calificacion_promedio
            FROM propuestas p
            JOIN usuarios u   ON p.id_freelancer = u.id_usuario
            JOIN proyectos pr ON p.id_proyecto   = pr.id_proyecto
            LEFT JOIN contratos ct  ON ct.id_propuesta  = p.id_propuesta
            LEFT JOIN calificaciones cal ON cal.id_contrato = ct.id_contrato
            WHERE p.id_proyecto = ?
            GROUP BY p.id_propuesta
            """;
        List<Propuesta> lista = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idProyecto);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public List<Propuesta> findByFreelancer(int idFreelancer) throws SQLException {
        String sql = """
            SELECT p.*, u.nombre_completo AS nombre_freelancer,
                   pr.titulo AS titulo_proyecto,
                   0.0 AS calificacion_promedio
            FROM propuestas p
            JOIN usuarios u   ON p.id_freelancer = u.id_usuario
            JOIN proyectos pr ON p.id_proyecto   = pr.id_proyecto
            WHERE p.id_freelancer = ?
            """;
        List<Propuesta> lista = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idFreelancer);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public boolean updateEstado(int idPropuesta, String estado) throws SQLException {
        String sql = "UPDATE propuestas SET estado=? WHERE id_propuesta=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, estado); ps.setInt(2, idPropuesta);
            return ps.executeUpdate() > 0;
        }
    }

    public void rechazarOtras(int idProyecto, int idPropuestaAceptada) throws SQLException {
        String sql = """
            UPDATE propuestas SET estado='RECHAZADA'
            WHERE id_proyecto=? AND id_propuesta != ? AND estado='PENDIENTE'
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idProyecto); ps.setInt(2, idPropuestaAceptada);
            ps.executeUpdate();
        }
    }

    public boolean existePropuestaFreelancer(int idProyecto, int idFreelancer) throws SQLException {
        String sql = "SELECT 1 FROM propuestas WHERE id_proyecto=? AND id_freelancer=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idProyecto); ps.setInt(2, idFreelancer);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public boolean delete(int idPropuesta) throws SQLException {
        String sql = "DELETE FROM propuestas WHERE id_propuesta=? AND estado='PENDIENTE'";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPropuesta);
            return ps.executeUpdate() > 0;
        }
    }

    private Propuesta mapRow(ResultSet rs) throws SQLException {
        Propuesta p = new Propuesta();
        p.setIdPropuesta      (rs.getInt       ("id_propuesta"));
        p.setIdProyecto       (rs.getInt       ("id_proyecto"));
        p.setIdFreelancer     (rs.getInt       ("id_freelancer"));
        p.setMontoOfertado    (rs.getBigDecimal("monto_ofertado"));
        p.setPlazoEntregaDias (rs.getInt       ("plazo_entrega_dias"));
        p.setCartaPresentacion(rs.getString    ("carta_presentacion"));
        p.setEstado           (rs.getString    ("estado"));
        try { p.setNombreFreelancer    (rs.getString("nombre_freelancer"));    } catch (SQLException ignored) {}
        try { p.setTituloProyecto      (rs.getString("titulo_proyecto"));      } catch (SQLException ignored) {}
        try { p.setCalificacionPromedio(rs.getDouble("calificacion_promedio")); } catch (SQLException ignored) {}
        return p;
    }
}
