/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.dao;

import com.connectwork.config.DBConnection;
import com.connectwork.model.Contrato;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author braya
 */
public class ContratoDAO {
    public int insert(Contrato ct) throws SQLException {
        String sql = """
                     
            INSERT INTO contratos (id_propuesta, fecha_inicio, monto_bloqueado, estado)
            VALUES (?, CURRENT_TIMESTAMP, ?, 'ACTIVO')
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt       (1, ct.getIdPropuesta());
            ps.setBigDecimal(2, ct.getMontoBlockeado());
            ps.executeUpdate();
            try (ResultSet gen = ps.getGeneratedKeys()) {
                if (gen.next()) return gen.getInt(1);
            }
        }
        return -1;
    }

    public Contrato findById(int id) throws SQLException {
        String sql = """
            SELECT ct.*, pr.titulo AS titulo_proyecto,
                   uf.nombre_completo AS nombre_freelancer,
                   uc.nombre_completo AS nombre_cliente
            FROM contratos ct
            JOIN propuestas p  ON ct.id_propuesta  = p.id_propuesta
            JOIN proyectos pr  ON p.id_proyecto    = pr.id_proyecto
            JOIN usuarios uf   ON p.id_freelancer  = uf.id_usuario
            JOIN usuarios uc   ON pr.id_cliente    = uc.id_usuario
            WHERE ct.id_contrato = ?
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

    public List<Contrato> findByFreelancer(int idFreelancer) throws SQLException {
        String sql = """
            SELECT ct.*, pr.titulo AS titulo_proyecto,
                   uf.nombre_completo AS nombre_freelancer,
                   uc.nombre_completo AS nombre_cliente
            FROM contratos ct
            JOIN propuestas p  ON ct.id_propuesta = p.id_propuesta
            JOIN proyectos pr  ON p.id_proyecto   = pr.id_proyecto
            JOIN usuarios uf   ON p.id_freelancer = uf.id_usuario
            JOIN usuarios uc   ON pr.id_cliente   = uc.id_usuario
            WHERE p.id_freelancer = ?
            """;
        return queryLista(sql, idFreelancer);
    }

    public List<Contrato> findByCliente(int idCliente) throws SQLException {
        String sql = """
            SELECT ct.*, pr.titulo AS titulo_proyecto,
                   uf.nombre_completo AS nombre_freelancer,
                   uc.nombre_completo AS nombre_cliente
            FROM contratos ct
            JOIN propuestas p  ON ct.id_propuesta = p.id_propuesta
            JOIN proyectos pr  ON p.id_proyecto   = pr.id_proyecto
            JOIN usuarios uf   ON p.id_freelancer = uf.id_usuario
            JOIN usuarios uc   ON pr.id_cliente   = uc.id_usuario
            WHERE pr.id_cliente = ?
            """;
        return queryLista(sql, idCliente);
    }

    public boolean cancelar(int idContrato, String motivo) throws SQLException {
        String sql = """
            UPDATE contratos SET estado='CANCELADO', motivo_cancelacion=?
            WHERE id_contrato=?
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, motivo); ps.setInt(2, idContrato);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean registrarComision(int idContrato, BigDecimal comision) throws SQLException {
        String sql = """
            UPDATE contratos SET estado='COMPLETADO', comision_aplicada=?
            WHERE id_contrato=?
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBigDecimal(1, comision); ps.setInt(2, idContrato);
            return ps.executeUpdate() > 0;
        }
    }

    private List<Contrato> queryLista(String sql, int param) throws SQLException {
        List<Contrato> lista = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    private Contrato mapRow(ResultSet rs) throws SQLException {
        Contrato ct = new Contrato();
        ct.setIdContrato      (rs.getInt       ("id_contrato"));
        ct.setIdPropuesta     (rs.getInt       ("id_propuesta"));
        ct.setFechaInicio     (rs.getTimestamp ("fecha_inicio"));
        ct.setMontoBlockeado  (rs.getBigDecimal("monto_bloqueado"));
        ct.setEstado          (rs.getString    ("estado"));
        ct.setMotivoCancelacion(rs.getString   ("motivo_cancelacion"));
        ct.setComisionAplicada(rs.getBigDecimal("comision_aplicada"));
        try { ct.setTituloProyecto  (rs.getString("titulo_proyecto"));   } catch (SQLException ignored) {}
        try { ct.setNombreFreelancer(rs.getString("nombre_freelancer")); } catch (SQLException ignored) {}
        try { ct.setNombreCliente   (rs.getString("nombre_cliente"));    } catch (SQLException ignored) {}
        return ct;
    }
}
