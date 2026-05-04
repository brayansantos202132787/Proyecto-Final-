/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.service;

import com.connectwork.dao.ContratoDAO;
import com.connectwork.dao.ProyectoDAO;
import com.connectwork.model.Proyecto;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author braya
 */
public class ProyectoService {
    private final ProyectoDAO proyectoDAO = new ProyectoDAO();
    private final ContratoDAO contratoDAO = new ContratoDAO();

    public List<Proyecto> listarAbiertos(Integer idCategoria, Integer idHabilidad,
                                         Double presMin, Double presMax) throws SQLException {
        List<Proyecto> lista = proyectoDAO.findAbiertos(idCategoria, idHabilidad, presMin, presMax);
        
        for (Proyecto p : lista) {
            p.setHabilidadesRequeridas(proyectoDAO.findHabilidades(p.getIdProyecto()));
        }
        return lista;
    }

    public List<Proyecto> listarPorCliente(int idCliente) throws SQLException {
        List<Proyecto> lista = proyectoDAO.findByCliente(idCliente);
        for (Proyecto p : lista) {
            p.setHabilidadesRequeridas(proyectoDAO.findHabilidades(p.getIdProyecto()));
        }
        return lista;
    }

    public Proyecto obtenerDetalle(int idProyecto) throws SQLException {
        Proyecto p = proyectoDAO.findById(idProyecto);
        if (p != null) {
            p.setHabilidadesRequeridas(proyectoDAO.findHabilidades(idProyecto));
        }
        return p;
    }

    public Proyecto crearProyecto(Proyecto p) throws SQLException {
        validarProyecto(p);
        int id = proyectoDAO.insert(p);
        p.setIdProyecto(id);
        p.setEstado("ABIERTO");

        // Insertar habilidades requeridas
        if (p.getHabilidadesRequeridas() != null) {
            for (int idHabilidad : p.getHabilidadesRequeridas()) {
                proyectoDAO.insertHabilidad(id, idHabilidad);
            }
        }
        return p;
    }

    public boolean editarProyecto(Proyecto p, int idCliente) throws SQLException {
        Proyecto existente = proyectoDAO.findById(p.getIdProyecto());
        if (existente == null)
            throw new IllegalArgumentException("Proyecto no encontrado");
        if (existente.getIdCliente() != idCliente)
            throw new IllegalArgumentException("No autorizado");
        if (!"ABIERTO".equals(existente.getEstado()))
            throw new IllegalStateException("Solo se pueden editar proyectos en estado ABIERTO");

        validarProyecto(p);
        return proyectoDAO.update(p);
    }

    public void cancelarProyecto(int idProyecto, int idCliente) throws SQLException {
        Proyecto p = proyectoDAO.findById(idProyecto);
        if (p == null) throw new IllegalArgumentException("Proyecto no encontrado");
        if (p.getIdCliente() != idCliente) throw new IllegalArgumentException("No autorizado");

        // No cancelar si tiene contrato activo
        List<?> contratos = contratoDAO.findByCliente(idCliente);
        boolean tieneContratoActivo = contratos.stream().anyMatch(ct -> {
            com.connectwork.model.Contrato c = (com.connectwork.model.Contrato) ct;
            return "ACTIVO".equals(c.getEstado())
                && c.getIdPropuesta() > 0;
        });
        if (tieneContratoActivo)
            throw new IllegalStateException("No se puede cancelar un proyecto con contrato activo");

        proyectoDAO.updateEstado(idProyecto, "CANCELADO");
    }

    private void validarProyecto(Proyecto p) {
        if (p.getTitulo() == null || p.getTitulo().isBlank())
            throw new IllegalArgumentException("El título es obligatorio");
        if (p.getDescripcion() == null || p.getDescripcion().isBlank())
            throw new IllegalArgumentException("La descripción es obligatoria");
        if (p.getPresupuestoMaximo() == null)
            throw new IllegalArgumentException("El presupuesto máximo es obligatorio");
        if (p.getFechaLimite() == null)
            throw new IllegalArgumentException("La fecha límite es obligatoria");
        if (p.getIdCategoria() <= 0)
            throw new IllegalArgumentException("La categoría es obligatoria");
    }
}
