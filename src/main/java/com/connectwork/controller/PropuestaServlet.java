/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.controller;

import com.connectwork.model.Propuesta;
import com.connectwork.service.PropuestaService;
import com.connectwork.util.JsonUtil;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author braya
 */
public class PropuestaServlet extends HttpServlet {
    private final PropuestaService propuestaService = new PropuestaService();

    // GET /api/propuestas?proyecto={id}  → propuestas de un proyecto (cliente)
    // GET /api/propuestas/mias           → propuestas del freelancer autenticado
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int    userId = (int)    req.getAttribute("userId");
            String role   = (String) req.getAttribute("role");
            String path   = req.getPathInfo();

            if ("/mias".equals(path)) {
                List<Propuesta> lista = propuestaService.listarPorFreelancer(userId);
                JsonUtil.sendSuccess(resp, 200, "OK", lista);
            } else {
                String idProyectoStr = req.getParameter("proyecto");
                if (idProyectoStr == null)
                    throw new IllegalArgumentException("Parámetro 'proyecto' requerido");
                int idProyecto = Integer.parseInt(idProyectoStr);
                List<Propuesta> lista = propuestaService.listarPorProyecto(idProyecto, userId, role);
                JsonUtil.sendSuccess(resp, 200, "OK", lista);
            }
        } catch (IllegalArgumentException e) {
            JsonUtil.sendError(resp, 400, e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }

    // POST /api/propuestas              → enviar propuesta (freelancer)
    // POST /api/propuestas/{id}/aceptar → aceptar propuesta (cliente)
    // POST /api/propuestas/{id}/rechazar → rechazar propuesta (cliente)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int    userId = (int)    req.getAttribute("userId");
            String role   = (String) req.getAttribute("role");
            String path   = req.getPathInfo();

            if (path == null || "/".equals(path)) {
                // Freelancer envía propuesta
                if (!"FREELANCER".equals(role)) {
                    JsonUtil.sendError(resp, 403, "Solo freelancers pueden enviar propuestas");
                    return;
                }
                Propuesta p = JsonUtil.fromRequest(req, Propuesta.class);
                p.setIdFreelancer(userId);
                propuestaService.enviarPropuesta(p);
                JsonUtil.sendSuccess(resp, 201, "Propuesta enviada", null);

            } else if (path.endsWith("/aceptar")) {
                int idPropuesta = Integer.parseInt(path.replace("/", "").replace("aceptar", "").trim());
                propuestaService.aceptarPropuesta(idPropuesta, userId);
                JsonUtil.sendSuccess(resp, 200, "Propuesta aceptada y contrato generado", null);

            } else if (path.endsWith("/rechazar")) {
                int idPropuesta = Integer.parseInt(path.replace("/", "").replace("rechazar", "").trim());
                propuestaService.rechazarPropuesta(idPropuesta, userId);
                JsonUtil.sendSuccess(resp, 200, "Propuesta rechazada", null);
            }

        } catch (IllegalArgumentException | IllegalStateException e) {
            JsonUtil.sendError(resp, 400, e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }

    // DELETE /api/propuestas/{id} → retirar propuesta (freelancer)
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int    userId      = (int)    req.getAttribute("userId");
            String path        = req.getPathInfo();
            int    idPropuesta = Integer.parseInt(path.substring(1));

            propuestaService.retirarPropuesta(idPropuesta, userId);
            JsonUtil.sendSuccess(resp, 200, "Propuesta retirada", null);

        } catch (IllegalStateException e) {
            JsonUtil.sendError(resp, 409, e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }
}
