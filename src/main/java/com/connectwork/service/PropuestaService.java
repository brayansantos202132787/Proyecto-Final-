/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.service;

import com.connectwork.dao.PerfilFreelancerDAO;
import com.connectwork.dao.PropuestaDAO;
import com.connectwork.dao.ProyectoDAO;
import com.connectwork.model.Propuesta;
import com.connectwork.model.Proyecto;
import com.connectwork.service.ContratoService;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author braya
 */
public class PropuestaService {
     private final PropuestaDAO       propuestaDAO       = new PropuestaDAO();
    private final ProyectoDAO        proyectoDAO        = new ProyectoDAO();
    private final PerfilFreelancerDAO perfilFreelancerDAO = new PerfilFreelancerDAO();
    private final ContratoService    contratoService    = new ContratoService();

    public void enviarPropuesta(Propuesta p) throws SQLException {
        // Validar que el proyecto exista y esté abierto
        Proyecto proyecto = proyectoDAO.findById(p.getIdProyecto());
        if (proyecto == null)
            throw new IllegalArgumentException("Proyecto no encontrado");
        if (!"ABIERTO".equals(proyecto.getEstado()))
            throw new IllegalStateException("El proyecto no está abierto");

        // Validar que el monto no supere el presupuesto máximo
        if (p.getMontoOfertado().compareTo(proyecto.getPresupuestoMaximo()) > 0)
            throw new IllegalArgumentException("El monto ofertado supera el presupuesto máximo");

        // Validar que el freelancer no haya enviado ya una propuesta
        if (propuestaDAO.existePropuestaFreelancer(p.getIdProyecto(), p.getIdFreelancer()))
            throw new IllegalStateException("Ya enviaste una propuesta a este proyecto");

        // Validar que el freelancer tenga perfil completo
        if (!perfilFreelancerDAO.exists(p.getIdFreelancer()))
            throw new IllegalStateException("Debes completar tu perfil antes de enviar propuestas");

        // Validar que el freelancer tenga al menos una habilidad requerida
        List<Integer> habilidadesProyecto    = proyectoDAO.findHabilidades(p.getIdProyecto());
        List<Integer> habilidadesFreelancer  = perfilFreelancerDAO.findHabilidades(p.getIdFreelancer());
        boolean tieneHabilidad = habilidadesProyecto.stream()
                .anyMatch(habilidadesFreelancer::contains);
        if (!tieneHabilidad)
            throw new IllegalStateException("No cumples con las habilidades requeridas para este proyecto");

        // Validar campos obligatorios
        if (p.getPlazoEntregaDias() <= 0)
            throw new IllegalArgumentException("El plazo de entrega debe ser mayor a 0");
        if (p.getCartaPresentacion() == null || p.getCartaPresentacion().isBlank())
            throw new IllegalArgumentException("La carta de presentación es obligatoria");

        propuestaDAO.insert(p);
    }

    public void aceptarPropuesta(int idPropuesta, int idCliente) throws SQLException {
        contratoService.aceptarPropuesta(idPropuesta, idCliente);
    }

    public void rechazarPropuesta(int idPropuesta, int idCliente) throws SQLException {
        Propuesta propuesta = propuestaDAO.findById(idPropuesta);
        if (propuesta == null)
            throw new IllegalArgumentException("Propuesta no encontrada");

        Proyecto proyecto = proyectoDAO.findById(propuesta.getIdProyecto());
        if (proyecto == null || proyecto.getIdCliente() != idCliente)
            throw new IllegalArgumentException("No autorizado");

        propuestaDAO.updateEstado(idPropuesta, "RECHAZADA");
    }

    public void retirarPropuesta(int idPropuesta, int idFreelancer) throws SQLException {
        Propuesta propuesta = propuestaDAO.findById(idPropuesta);
        if (propuesta == null)
            throw new IllegalArgumentException("Propuesta no encontrada");
        if (propuesta.getIdFreelancer() != idFreelancer)
            throw new IllegalArgumentException("No autorizado");

        Proyecto proyecto = proyectoDAO.findById(propuesta.getIdProyecto());
        if (!"ABIERTO".equals(proyecto.getEstado()))
            throw new IllegalStateException("Solo puedes retirar propuestas de proyectos abiertos");

        boolean eliminada = propuestaDAO.delete(idPropuesta);
        if (!eliminada)
            throw new IllegalStateException("No se pudo retirar la propuesta");
    }

    public List<Propuesta> listarPorProyecto(int idProyecto, int idCliente, String role) throws SQLException {
        // Solo el cliente dueño del proyecto o un admin pueden ver las propuestas
        if ("CLIENTE".equals(role)) {
            Proyecto proyecto = proyectoDAO.findById(idProyecto);
            if (proyecto == null || proyecto.getIdCliente() != idCliente)
                throw new IllegalArgumentException("No autorizado");
        }
        return propuestaDAO.findByProyecto(idProyecto);
    }

    public List<Propuesta> listarPorFreelancer(int idFreelancer) throws SQLException {
        return propuestaDAO.findByFreelancer(idFreelancer);
    }
}
