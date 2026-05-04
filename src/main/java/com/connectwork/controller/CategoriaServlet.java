/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.controller;

import com.connectwork.model.Categoria;
import com.connectwork.service.CategoriaService;
import com.connectwork.util.JsonUtil;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Map;

/**
 *
 * @author braya
 */
public class CategoriaServlet extends HttpServlet {

    private final CategoriaService categoriaService = new CategoriaService();

    // GET /api/categorias → listar todas
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            JsonUtil.sendSuccess(resp, 200, "OK", categoriaService.listarTodas());
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }

    // POST /api/categorias → crear (admin)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String   role = (String) req.getAttribute("role");
            Categoria cat = JsonUtil.fromRequest(req, Categoria.class);
            Categoria creada = categoriaService.crear(cat, role);
            JsonUtil.sendSuccess(resp, 201, "Categoría creada", creada);
        } catch (SecurityException e) {
            JsonUtil.sendError(resp, 403, e.getMessage());
        } catch (IllegalArgumentException e) {
            JsonUtil.sendError(resp, 400, e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }

    // PUT /api/categorias/{id} → editar (admin)
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String   role = (String) req.getAttribute("role");
            String   path = req.getPathInfo();
            int      id   = Integer.parseInt(path.substring(1));
            Categoria cat = JsonUtil.fromRequest(req, Categoria.class);
            cat.setIdCategoria(id);
            categoriaService.editar(cat, role);
            JsonUtil.sendSuccess(resp, 200, "Categoría actualizada", null);
        } catch (SecurityException e) {
            JsonUtil.sendError(resp, 403, e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }

    // PATCH /api/categorias/{id}/estado → activar/desactivar (admin)
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws jakarta.servlet.ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) doPatch(req, resp);
        else super.service(req, resp);
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String   role = (String) req.getAttribute("role");
            String   path = req.getPathInfo();
            int      id   = Integer.parseInt(path.replace("/", "").replace("estado", "").trim());
            Map<?,?> body = JsonUtil.fromRequest(req, Map.class);
            boolean  activa = (Boolean) body.get("activa");
            categoriaService.toggleActiva(id, activa, role);
            JsonUtil.sendSuccess(resp, 200, "Estado actualizado", null);
        } catch (SecurityException e) {
            JsonUtil.sendError(resp, 403, e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }
}
