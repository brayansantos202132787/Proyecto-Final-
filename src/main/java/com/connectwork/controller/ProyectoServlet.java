/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.controller;

import com.connectwork.model.Proyecto;
import com.connectwork.service.ProyectoService;
import com.connectwork.util.JsonUtil;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author braya
 */
public class ProyectoServlet extends HttpServlet {
    
     private final ProyectoService proyectoService = new ProyectoService();

    // GET /api/proyectos          → listar (abiertos para freelancer, propios para cliente)
    // GET /api/proyectos/{id}     → detalle
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int    userId = (int)    req.getAttribute("userId");
            String role   = (String) req.getAttribute("role");
            String path   = req.getPathInfo();

            if (path == null || "/".equals(path)) {
                // Listar proyectos
                if ("FREELANCER".equals(role)) {
                    // Proyectos abiertos con filtros opcionales
                    Integer idCategoria = parseIntParam(req, "categoria");
                    Integer idHabilidad = parseIntParam(req, "habilidad");
                    Double  presMin     = parseDoubleParam(req, "presMin");
                    Double  presMax     = parseDoubleParam(req, "presMax");
                    List<Proyecto> lista = proyectoService.listarAbiertos(idCategoria, idHabilidad, presMin, presMax);
                    JsonUtil.sendSuccess(resp, 200, "OK", lista);
                } else if ("CLIENTE".equals(role)) {
                    List<Proyecto> lista = proyectoService.listarPorCliente(userId);
                    JsonUtil.sendSuccess(resp, 200, "OK", lista);
                } else {
                    // Admin puede ver todos
                    List<Proyecto> lista = proyectoService.listarAbiertos(null, null, null, null);
                    JsonUtil.sendSuccess(resp, 200, "OK", lista);
                }
            } else {
                int idProyecto = Integer.parseInt(path.substring(1));
                Proyecto p = proyectoService.obtenerDetalle(idProyecto);
                if (p == null) JsonUtil.sendError(resp, 404, "Proyecto no encontrado");
                else           JsonUtil.sendSuccess(resp, 200, "OK", p);
            }
        } catch (NumberFormatException e) {
            JsonUtil.sendError(resp, 400, "ID inválido");
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }

    // POST /api/proyectos → crear proyecto (solo CLIENTE)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String role = (String) req.getAttribute("role");
            int userId  = (int)    req.getAttribute("userId");

            if (!"CLIENTE".equals(role)) {
                JsonUtil.sendError(resp, 403, "Solo clientes pueden crear proyectos");
                return;
            }

            Proyecto p = JsonUtil.fromRequest(req, Proyecto.class);
            p.setIdCliente(userId);

            Proyecto creado = proyectoService.crearProyecto(p);
            JsonUtil.sendSuccess(resp, 201, "Proyecto creado", creado);

        } catch (IllegalArgumentException e) {
            JsonUtil.sendError(resp, 400, e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }

    // PUT /api/proyectos/{id} → editar proyecto
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int    userId = (int)    req.getAttribute("userId");
            String path   = req.getPathInfo();
            int idProyecto = Integer.parseInt(path.substring(1));

            Proyecto p = JsonUtil.fromRequest(req, Proyecto.class);
            p.setIdProyecto(idProyecto);
            p.setIdCliente(userId);

            boolean ok = proyectoService.editarProyecto(p, userId);
            if (ok) JsonUtil.sendSuccess(resp, 200, "Proyecto actualizado", null);
            else    JsonUtil.sendError  (resp, 400, "No se puede editar");

        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }

    // DELETE /api/proyectos/{id} → cancelar proyecto
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int    userId     = (int)    req.getAttribute("userId");
            String path       = req.getPathInfo();
            int    idProyecto = Integer.parseInt(path.substring(1));

            proyectoService.cancelarProyecto(idProyecto, userId);
            JsonUtil.sendSuccess(resp, 200, "Proyecto cancelado", null);

        } catch (IllegalStateException e) {
            JsonUtil.sendError(resp, 409, e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }

    private Integer parseIntParam(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v != null ? Integer.parseInt(v) : null;
    }

    private Double parseDoubleParam(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v != null ? Double.parseDouble(v) : null;
    }
    
}
