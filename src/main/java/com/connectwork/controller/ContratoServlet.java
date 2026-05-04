/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.controller;

import com.connectwork.dao.ContratoDAO;
import com.connectwork.model.Contrato;
import com.connectwork.service.ContratoService;
import com.connectwork.util.JsonUtil;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author braya
 */
public class ContratoServlet extends HttpServlet {

    private final ContratoDAO    contratoDAO    = new ContratoDAO();
    private final ContratoService contratoService = new ContratoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int    userId = (int)    req.getAttribute("userId");
            String role   = (String) req.getAttribute("role");
            String path   = req.getPathInfo();

            if (path != null && !"/".equals(path)) {
                int idContrato = Integer.parseInt(path.substring(1));
                Contrato ct = contratoDAO.findById(idContrato);
                if (ct == null) { JsonUtil.sendError(resp, 404, "Contrato no encontrado"); return; }
                JsonUtil.sendSuccess(resp, 200, "OK", ct);
                return;
            }

            List<Contrato> lista;
            if ("FREELANCER".equals(role)) {
                lista = contratoDAO.findByFreelancer(userId);
            } else {
                lista = contratoDAO.findByCliente(userId);
            }
            JsonUtil.sendSuccess(resp, 200, "OK", lista);

        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }

    // POST /api/contratos/{id}/cancelar → cliente cancela contrato
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int    userId = (int)    req.getAttribute("userId");
            String path   = req.getPathInfo(); // /{id}/cancelar

            if (path != null && path.endsWith("/cancelar")) {
                int idContrato = Integer.parseInt(path.split("/")[1]);
                Map<?,?> body  = JsonUtil.fromRequest(req, Map.class);
                String motivo  = (String) body.get("motivo");

                if (motivo == null || motivo.isBlank())
                    throw new IllegalArgumentException("El motivo de cancelación es obligatorio");

                contratoService.cancelarContrato(idContrato, userId, motivo);
                JsonUtil.sendSuccess(resp, 200, "Contrato cancelado", null);
            } else {
                JsonUtil.sendError(resp, 404, "Ruta no encontrada");
            }

        } catch (IllegalArgumentException e) {
            JsonUtil.sendError(resp, 400, e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }
}