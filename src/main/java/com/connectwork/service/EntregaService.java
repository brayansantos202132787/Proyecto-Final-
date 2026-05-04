/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.service;

import com.connectwork.dao.ContratoDAO;
import com.connectwork.dao.EntregaDAO;
import com.connectwork.dao.PropuestaDAO;
import com.connectwork.dao.ProyectoDAO;
import com.connectwork.model.Contrato;
import com.connectwork.model.Entrega;
import com.connectwork.model.Propuesta;
import com.connectwork.model.Proyecto;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author braya
 */
public class EntregaService {
    private final EntregaDAO  entregaDAO  = new EntregaDAO();
    private final ContratoDAO contratoDAO = new ContratoDAO();
    private final PropuestaDAO propuestaDAO = new PropuestaDAO();
    private final ProyectoDAO proyectoDAO  = new ProyectoDAO();

    public void subirEntrega(Entrega e, int idFreelancer) throws SQLException {
        Contrato contrato = contratoDAO.findById(e.getIdContrato());
        if (contrato == null)
            throw new IllegalArgumentException("Contrato no encontrado");
        if (!"ACTIVO".equals(contrato.getEstado()))
            throw new IllegalStateException("El contrato no está activo");

        // Verificar que el freelancer sea el del contrato
        Propuesta propuesta = propuestaDAO.findById(contrato.getIdPropuesta());
        if (propuesta.getIdFreelancer() != idFreelancer)
            throw new IllegalArgumentException("No autorizado");

        // Validar campos
        if (e.getDescripcion() == null || e.getDescripcion().isBlank())
            throw new IllegalArgumentException("La descripción de la entrega es obligatoria");

        entregaDAO.insert(e);

        // Cambiar estado del proyecto a ENTREGA_PENDIENTE
        proyectoDAO.updateEstado(propuesta.getIdProyecto(), "ENTREGA_PENDIENTE");
    }

    public void rechazarEntrega(int idEntrega, int idCliente, String motivo) throws SQLException {
        if (motivo == null || motivo.isBlank())
            throw new IllegalArgumentException("El motivo de rechazo es obligatorio");

        Entrega entrega = entregaDAO.findById(idEntrega);
        if (entrega == null)
            throw new IllegalArgumentException("Entrega no encontrada");

        Contrato contrato = contratoDAO.findById(entrega.getIdContrato());
        Propuesta propuesta = propuestaDAO.findById(contrato.getIdPropuesta());
        Proyecto proyecto = proyectoDAO.findById(propuesta.getIdProyecto());

        if (proyecto.getIdCliente() != idCliente)
            throw new IllegalArgumentException("No autorizado");

        entregaDAO.updateEstado(idEntrega, "RECHAZADA", motivo);

        // Volver el proyecto a EN_PROGRESO para que el freelancer pueda corregir
        proyectoDAO.updateEstado(proyecto.getIdProyecto(), "EN_PROGRESO");
    }

    public List<Entrega> listarPorContrato(int idContrato) throws SQLException {
        return entregaDAO.findByContrato(idContrato);
    }

    public int getContratoDeEntrega(int idEntrega) throws SQLException {
        return entregaDAO.getContratoDeEntrega(idEntrega);
    }
}
