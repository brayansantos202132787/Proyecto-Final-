/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.service;

import com.connectwork.dao.HabilidadDAO;
import com.connectwork.dao.SolicitudHabilidadDAO;
import com.connectwork.model.Habilidad;
import com.connectwork.model.SolicitudHabilidad;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author braya
 */
public class HabilidadService {
    private final HabilidadDAO         habilidadDAO         = new HabilidadDAO();
    private final SolicitudHabilidadDAO solicitudHabilidadDAO = new SolicitudHabilidadDAO();

    public List<Habilidad> listarTodas() throws SQLException {
        return habilidadDAO.findAll();
    }

    public List<Habilidad> listarPorCategoria(int idCategoria) throws SQLException {
        return habilidadDAO.findByCategoria(idCategoria);
    }

    public Habilidad crear(Habilidad h, String role) throws SQLException {
        if (!"ADMINISTRADOR".equals(role))
            throw new SecurityException("Solo el administrador puede crear habilidades");
        if (h.getNombre() == null || h.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio");
        if (h.getIdCategoria() <= 0)
            throw new IllegalArgumentException("La categoría es obligatoria");
        int id = habilidadDAO.insert(h);
        h.setIdHabilidad(id);
        h.setActiva(true);
        return h;
    }

    public boolean editar(Habilidad h, String role) throws SQLException {
        if (!"ADMINISTRADOR".equals(role))
            throw new SecurityException("Solo el administrador puede editar habilidades");
        return habilidadDAO.update(h);
    }

    public boolean toggleActiva(int idHabilidad, boolean activa, String role) throws SQLException {
        if (!"ADMINISTRADOR".equals(role))
            throw new SecurityException("Solo el administrador puede desactivar habilidades");
        return habilidadDAO.toggleActiva(idHabilidad, activa);
    }

    // Solicitudes de habilidad (freelancer → admin)
    public void crearSolicitud(SolicitudHabilidad s) throws SQLException {
        if (s.getNombre() == null || s.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre de la habilidad solicitada es obligatorio");
        solicitudHabilidadDAO.insert(s);
    }

    public List<SolicitudHabilidad> listarSolicitudes(String role) throws SQLException {
        if (!"ADMINISTRADOR".equals(role))
            throw new SecurityException("No autorizado");
        return solicitudHabilidadDAO.findAll();
    }

    public void resolverSolicitud(int idSolicitud, String decision, String role) throws SQLException {
        if (!"ADMINISTRADOR".equals(role))
            throw new SecurityException("No autorizado");
        if (!"ACEPTADA".equals(decision) && !"RECHAZADA".equals(decision))
            throw new IllegalArgumentException("Decisión inválida");

        if ("ACEPTADA".equals(decision)) {
            List<SolicitudHabilidad> solicitudes = solicitudHabilidadDAO.findAll();
            solicitudes.stream()
                .filter(s -> s.getIdSolicitud() == idSolicitud)
                .findFirst()
                .ifPresent(s -> {
                    try {
                        Habilidad nueva = new Habilidad();
                        nueva.setNombre(s.getNombre());
                        habilidadDAO.insert(nueva);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
        }
        solicitudHabilidadDAO.updateEstado(idSolicitud, decision);
    }
}
