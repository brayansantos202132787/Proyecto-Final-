/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.controller;

import com.connectwork.dao.PerfilClienteDAO;
import com.connectwork.model.PerfilCliente;
import com.connectwork.util.JsonUtil;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 *
 * @author braya
 */
public class PerfilClienteServlet extends HttpServlet {

    private final PerfilClienteDAO perfilClienteDAO = new PerfilClienteDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int    userId = (int)    req.getAttribute("userId");
            String path   = req.getPathInfo();

            int idTarget = (path == null || "/".equals(path))
                           ? userId
                           : Integer.parseInt(path.substring(1));

            PerfilCliente p = perfilClienteDAO.findById(idTarget);
            if (p == null) JsonUtil.sendSuccess(resp, 200, "Perfil no completado", null);
            else           JsonUtil.sendSuccess(resp, 200, "OK", p);

        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int    userId = (int) req.getAttribute("userId");
            String role   = (String) req.getAttribute("role");

            if (!"CLIENTE".equals(role)) {
                JsonUtil.sendError(resp, 403, "Solo clientes pueden completar este perfil");
                return;
            }
            if (perfilClienteDAO.exists(userId)) {
                JsonUtil.sendError(resp, 409, "El perfil ya existe, usa PUT para editarlo");
                return;
            }

            PerfilCliente p = JsonUtil.fromRequest(req, PerfilCliente.class);
            p.setIdUsuario(userId);

            if (p.getDescripcionEmpresa() == null || p.getSector() == null)
                throw new IllegalArgumentException("Descripción y sector son obligatorios");

            perfilClienteDAO.insert(p);
            JsonUtil.sendSuccess(resp, 201, "Perfil creado", p);

        } catch (IllegalArgumentException e) {
            JsonUtil.sendError(resp, 400, e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }

    // PUT /api/perfil/cliente → actualizar perfil
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int    userId = (int) req.getAttribute("userId");
            String role   = (String) req.getAttribute("role");

            if (!"CLIENTE".equals(role)) {
                JsonUtil.sendError(resp, 403, "No autorizado");
                return;
            }

            PerfilCliente p = JsonUtil.fromRequest(req, PerfilCliente.class);
            p.setIdUsuario(userId);
            perfilClienteDAO.update(p);
            JsonUtil.sendSuccess(resp, 200, "Perfil actualizado", p);

        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }
}
