/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.service;

import com.connectwork.dao.CategoriaDAO;
import com.connectwork.dao.SolicitudCategoriaDAO;
import com.connectwork.model.Categoria;
import com.connectwork.model.SolicitudCategoria;

import java.sql.SQLException;
import java.util.List;



/**
 *
 * @author braya
 */
public class CategoriaService {
     private final CategoriaDAO         categoriaDAO         = new CategoriaDAO();
    private final SolicitudCategoriaDAO solicitudCategoriaDAO = new SolicitudCategoriaDAO();

    public List<Categoria> listarTodas() throws SQLException {
        return categoriaDAO.findAll();
    }

    public Categoria crear(Categoria cat, String role) throws SQLException {
        if (!"ADMINISTRADOR".equals(role))
            throw new SecurityException("Solo el administrador puede crear categorías");
        if (cat.getNombre() == null || cat.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio");
        int id = categoriaDAO.insert(cat);
        cat.setIdCategoria(id);
        cat.setActiva(true);
        return cat;
    }

    public boolean editar(Categoria cat, String role) throws SQLException {
        if (!"ADMINISTRADOR".equals(role))
            throw new SecurityException("Solo el administrador puede editar categorías");
        if (cat.getNombre() == null || cat.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio");
        return categoriaDAO.update(cat);
    }

    public boolean toggleActiva(int idCategoria, boolean activa, String role) throws SQLException {
        if (!"ADMINISTRADOR".equals(role))
            throw new SecurityException("Solo el administrador puede desactivar categorías");
        return categoriaDAO.toggleActiva(idCategoria, activa);
    }

    // Solicitudes de categoría (cliente → admin)
    public void crearSolicitud(SolicitudCategoria s) throws SQLException {
        if (s.getNombre() == null || s.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre de la categoría solicitada es obligatorio");
        solicitudCategoriaDAO.insert(s);
    }

    public List<SolicitudCategoria> listarSolicitudes(String role) throws SQLException {
        if (!"ADMINISTRADOR".equals(role))
            throw new SecurityException("No autorizado");
        return solicitudCategoriaDAO.findAll();
    }

    public void resolverSolicitud(int idSolicitud, String decision, String role) throws SQLException {
        if (!"ADMINISTRADOR".equals(role))
            throw new SecurityException("No autorizado");
        if (!"ACEPTADA".equals(decision) && !"RECHAZADA".equals(decision))
            throw new IllegalArgumentException("Decisión inválida");

        // Si se acepta, crear la categoría automáticamente
        if ("ACEPTADA".equals(decision)) {
            // Obtener todas las solicitudes para encontrar la que corresponde
            List<SolicitudCategoria> solicitudes = solicitudCategoriaDAO.findAll();
            solicitudes.stream()
                .filter(s -> s.getIdSolicitud() == idSolicitud)
                .findFirst()
                .ifPresent(s -> {
                    try {
                        Categoria nueva = new Categoria();
                        nueva.setNombre(s.getNombre());
                        categoriaDAO.insert(nueva);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
        }
        solicitudCategoriaDAO.updateEstado(idSolicitud, decision);
    }
}
