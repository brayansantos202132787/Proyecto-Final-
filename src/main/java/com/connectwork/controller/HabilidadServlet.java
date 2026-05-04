/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.controller;

import com.connectwork.model.Habilidad;
import com.connectwork.service.HabilidadService;
import com.connectwork.util.JsonUtil;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Map;
/**
 *
 * @author braya
 */
public class HabilidadServlet extends HttpServlet {

    private final HabilidadService habilidadService = new HabilidadService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String catParam = req.getParameter("categoria");
            if (catParam != null) {
                int idCat = Integer.parseInt(catParam);
                JsonUtil.sendSuccess(resp, 200, "OK", habilidadService.listarPorCategoria(idCat));
            } else {
                JsonUtil.sendSuccess(resp, 200, "OK", habilidadService.listarTodas());
            }
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }

    // POST /api/habilidades → crear (admin)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String    role = (String) req.getAttribute("role");
            Habilidad h    = JsonUtil.fromRequest(req, Habilidad.class);
            Habilidad creada = habilidadService.crear(h, role);
            JsonUtil.sendSuccess(resp, 201, "Habilidad creada", creada);
        } catch (SecurityException e) {
            JsonUtil.sendError(resp, 403, e.getMessage());
        } catch (IllegalArgumentException e) {
            JsonUtil.sendError(resp, 400, e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }

    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String    role = (String) req.getAttribute("role");
            String    path = req.getPathInfo();
            int       id   = Integer.parseInt(path.substring(1));
            Habilidad h    = JsonUtil.fromRequest(req, Habilidad.class);
            h.setIdHabilidad(id);
            habilidadService.editar(h, role);
            JsonUtil.sendSuccess(resp, 200, "Habilidad actualizada", null);
        } catch (SecurityException e) {
            JsonUtil.sendError(resp, 403, e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }

    // PATCH /api/habilidades/{id}/estado → activar/desactivar
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws jakarta.servlet.ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) doPatch(req, resp);
        else super.service(req, resp);
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String   role  = (String) req.getAttribute("role");
            String   path  = req.getPathInfo();
            int      id    = Integer.parseInt(path.replace("/", "").replace("estado", "").trim());
            Map<?,?> body  = JsonUtil.fromRequest(req, Map.class);
            boolean  activa = (Boolean) body.get("activa");
            habilidadService.toggleActiva(id, activa, role);
            JsonUtil.sendSuccess(resp, 200, "Estado actualizado", null);
        } catch (SecurityException e) {
            JsonUtil.sendError(resp, 403, e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }
}