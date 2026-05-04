/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.controller;

import com.connectwork.model.SolicitudCategoria;
import com.connectwork.model.SolicitudHabilidad;
import com.connectwork.service.CategoriaService;
import com.connectwork.service.HabilidadService;
import com.connectwork.util.JsonUtil;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Map;

/**
 *
 * @author braya
 */
public class SolicitudServlet extends HttpServlet {

    private final HabilidadService habilidadService = new HabilidadService();
    private final CategoriaService categoriaService = new CategoriaService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String role = (String) req.getAttribute("role");
            String path = req.getPathInfo();

            if ("/habilidades".equals(path)) {
                JsonUtil.sendSuccess(resp, 200, "OK",
                    habilidadService.listarSolicitudes(role));

            } else if ("/categorias".equals(path)) {
                JsonUtil.sendSuccess(resp, 200, "OK",
                    categoriaService.listarSolicitudes(role));

            } else {
                JsonUtil.sendError(resp, 404, "Ruta no encontrada");
            }

        } catch (SecurityException e) {
            JsonUtil.sendError(resp, 403, e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }

    // POST /api/solicitudes/habilidades → freelancer solicita nueva habilidad
    // POST /api/solicitudes/categorias  → cliente solicita nueva categoría
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int    userId = (int)    req.getAttribute("userId");
            String role   = (String) req.getAttribute("role");
            String path   = req.getPathInfo();

            if ("/habilidades".equals(path)) {
                if (!"FREELANCER".equals(role)) {
                    JsonUtil.sendError(resp, 403, "Solo freelancers pueden solicitar habilidades");
                    return;
                }
                SolicitudHabilidad s = JsonUtil.fromRequest(req, SolicitudHabilidad.class);
                s.setIdFreelancer(userId);
                habilidadService.crearSolicitud(s);
                JsonUtil.sendSuccess(resp, 201, "Solicitud enviada", null);

            } else if ("/categorias".equals(path)) {
                if (!"CLIENTE".equals(role)) {
                    JsonUtil.sendError(resp, 403, "Solo clientes pueden solicitar categorías");
                    return;
                }
                SolicitudCategoria s = JsonUtil.fromRequest(req, SolicitudCategoria.class);
                s.setIdCliente(userId);
                categoriaService.crearSolicitud(s);
                JsonUtil.sendSuccess(resp, 201, "Solicitud enviada", null);

            } else {
                JsonUtil.sendError(resp, 404, "Ruta no encontrada");
            }

        } catch (IllegalArgumentException e) {
            JsonUtil.sendError(resp, 400, e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }

    // PUT /api/solicitudes/habilidades/{id} → admin resuelve
    // PUT /api/solicitudes/categorias/{id}  → admin resuelve
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String   role = (String) req.getAttribute("role");
            String   path = req.getPathInfo();
            Map<?,?> body = JsonUtil.fromRequest(req, Map.class);
            String   decision = (String) body.get("decision"); // ACEPTADA | RECHAZADA

            if (path != null && path.startsWith("/habilidades/")) {
                int id = Integer.parseInt(path.replace("/habilidades/", ""));
                habilidadService.resolverSolicitud(id, decision, role);
                JsonUtil.sendSuccess(resp, 200, "Solicitud resuelta", null);

            } else if (path != null && path.startsWith("/categorias/")) {
                int id = Integer.parseInt(path.replace("/categorias/", ""));
                categoriaService.resolverSolicitud(id, decision, role);
                JsonUtil.sendSuccess(resp, 200, "Solicitud resuelta", null);

            } else {
                JsonUtil.sendError(resp, 404, "Ruta no encontrada");
            }

        } catch (SecurityException e) {
            JsonUtil.sendError(resp, 403, e.getMessage());
        } catch (IllegalArgumentException e) {
            JsonUtil.sendError(resp, 400, e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }
}