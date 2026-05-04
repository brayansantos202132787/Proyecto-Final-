/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.controller;

import com.connectwork.model.Usuario;
import com.connectwork.service.UsuarioService;
import com.connectwork.util.JsonUtil;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author braya
 */
public class UsuarioServlet extends HttpServlet {

    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int    userId = (int)    req.getAttribute("userId");
            String role   = (String) req.getAttribute("role");
            String path   = req.getPathInfo();

            if (path == null || "/".equals(path)) {
                if (!"ADMINISTRADOR".equals(role)) {
                    JsonUtil.sendError(resp, 403, "No autorizado");
                    return;
                }
                List<Usuario> lista = usuarioService.listarTodos();
                JsonUtil.sendSuccess(resp, 200, "OK", lista);

            } else if ("/me".equals(path)) {
                Usuario u = usuarioService.obtenerPorId(userId);
                u.setPasswordHash(null);
                JsonUtil.sendSuccess(resp, 200, "OK", u);

            } else {
                int idBuscado = Integer.parseInt(path.substring(1));
                if (!"ADMINISTRADOR".equals(role) && idBuscado != userId) {
                    JsonUtil.sendError(resp, 403, "No autorizado");
                    return;
                }
                Usuario u = usuarioService.obtenerPorId(idBuscado);
                u.setPasswordHash(null);
                JsonUtil.sendSuccess(resp, 200, "OK", u);
            }
        } catch (NumberFormatException e) {
            JsonUtil.sendError(resp, 400, "ID inválido");
        } catch (IllegalArgumentException e) {
            JsonUtil.sendError(resp, 404, e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }

    // PATCH /api/usuarios/{id}/estado → activar/desactivar (admin)
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws jakarta.servlet.ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String role = (String) req.getAttribute("role");
            if (!"ADMINISTRADOR".equals(role)) {
                JsonUtil.sendError(resp, 403, "No autorizado");
                return;
            }

            String path = req.getPathInfo(); // /{id}/estado
            String[] partes = path.split("/");
            int idUsuario = Integer.parseInt(partes[1]);

            Map<?, ?> body  = JsonUtil.fromRequest(req, Map.class);
            boolean   estado = (Boolean) body.get("estado");

            usuarioService.toggleEstado(idUsuario, estado);
            JsonUtil.sendSuccess(resp, 200, "Estado actualizado", null);

        } catch (IllegalArgumentException e) {
            JsonUtil.sendError(resp, 400, e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }
}
