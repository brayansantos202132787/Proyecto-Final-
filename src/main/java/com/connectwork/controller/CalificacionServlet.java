/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.controller;

import com.connectwork.dao.CalificacionDAO;
import com.connectwork.dao.ContratoDAO;
import com.connectwork.dao.PropuestaDAO;
import com.connectwork.dao.ProyectoDAO;
import com.connectwork.model.Calificacion;
import com.connectwork.model.Contrato;
import com.connectwork.model.Propuesta;
import com.connectwork.model.Proyecto;
import com.connectwork.util.JsonUtil;
import jakarta.servlet.http.*;

import java.io.IOException;



/**
 *
 * @author braya
 */
public class CalificacionServlet extends HttpServlet {

    private final CalificacionDAO calificacionDAO = new CalificacionDAO();
    private final ContratoDAO     contratoDAO     = new ContratoDAO();
    private final PropuestaDAO    propuestaDAO    = new PropuestaDAO();
    private final ProyectoDAO     proyectoDAO     = new ProyectoDAO();

    // POST /api/calificaciones → calificar freelancer (solo cliente, tras contrato completado)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int    userId = (int)    req.getAttribute("userId");
            String role   = (String) req.getAttribute("role");

            if (!"CLIENTE".equals(role)) {
                JsonUtil.sendError(resp, 403, "Solo clientes pueden calificar");
                return;
            }

            Calificacion cal = JsonUtil.fromRequest(req, Calificacion.class);

            // Validar estrellas
            if (cal.getEstrellas() < 1 || cal.getEstrellas() > 5)
                throw new IllegalArgumentException("La calificación debe ser entre 1 y 5 estrellas");

            // Verificar que el contrato pertenezca al cliente y esté completado
            Contrato contrato = contratoDAO.findById(cal.getIdContrato());
            if (contrato == null)
                throw new IllegalArgumentException("Contrato no encontrado");
            if (!"COMPLETADO".equals(contrato.getEstado()))
                throw new IllegalStateException("Solo se puede calificar en contratos completados");

            Propuesta propuesta = propuestaDAO.findById(contrato.getIdPropuesta());
            Proyecto  proyecto  = proyectoDAO.findById(propuesta.getIdProyecto());
            if (proyecto.getIdCliente() != userId)
                throw new IllegalArgumentException("No autorizado");

            // No calificar dos veces
            if (calificacionDAO.existeParaContrato(cal.getIdContrato()))
                throw new IllegalStateException("Ya calificaste este contrato");

            calificacionDAO.insert(cal);
            JsonUtil.sendSuccess(resp, 201, "Calificación registrada", null);

        } catch (IllegalArgumentException | IllegalStateException e) {
            JsonUtil.sendError(resp, 400, e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }

    // GET /api/calificaciones?freelancer={id} → ver calificaciones de un freelancer
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String idFreelancerStr = req.getParameter("freelancer");
            if (idFreelancerStr == null)
                throw new IllegalArgumentException("Parámetro 'freelancer' requerido");
            int idFreelancer = Integer.parseInt(idFreelancerStr);
            JsonUtil.sendSuccess(resp, 200, "OK", calificacionDAO.findByFreelancer(idFreelancer));
        } catch (IllegalArgumentException e) {
            JsonUtil.sendError(resp, 400, e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }
}
