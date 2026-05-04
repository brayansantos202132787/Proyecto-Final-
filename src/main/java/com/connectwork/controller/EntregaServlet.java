/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.controller;

import com.connectwork.model.Entrega;
import com.connectwork.service.ContratoService;
import com.connectwork.service.EntregaService;
import com.connectwork.util.JsonUtil;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author braya
 */
public class EntregaServlet extends HttpServlet {
    private final EntregaService  entregaService  = new EntregaService();
    private final ContratoService contratoService = new ContratoService();

    // GET /api/entregas?contrato={id} → historial de entregas de un contrato
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String idContratoStr = req.getParameter("contrato");
            if (idContratoStr == null) throw new IllegalArgumentException("Parámetro 'contrato' requerido");
            int idContrato = Integer.parseInt(idContratoStr);
            List<Entrega> lista = entregaService.listarPorContrato(idContrato);
            JsonUtil.sendSuccess(resp, 200, "OK", lista);
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }

    // POST /api/entregas           → subir entrega (freelancer)
    // POST /api/entregas/{id}/aprobar   → cliente aprueba
    // POST /api/entregas/{id}/rechazar  → cliente rechaza (body: { motivo })
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int    userId = (int)    req.getAttribute("userId");
            String role   = (String) req.getAttribute("role");
            String path   = req.getPathInfo();

            if (path == null || "/".equals(path)) {
                if (!"FREELANCER".equals(role)) {
                    JsonUtil.sendError(resp, 403, "Solo freelancers pueden subir entregas");
                    return;
                }
                Entrega e = JsonUtil.fromRequest(req, Entrega.class);
                entregaService.subirEntrega(e, userId);
                JsonUtil.sendSuccess(resp, 201, "Entrega registrada", null);

            } else if (path.endsWith("/aprobar")) {
                int idEntrega = Integer.parseInt(path.replaceAll("/?(\\d+)/aprobar", "$1"));
                int idContrato = entregaService.getContratoDeEntrega(idEntrega);
                contratoService.aprobarEntrega(idContrato, userId);
                JsonUtil.sendSuccess(resp, 200, "Entrega aprobada y pago liberado", null);

            } else if (path.endsWith("/rechazar")) {
                int idEntrega = Integer.parseInt(path.replaceAll("/?(\\d+)/rechazar", "$1"));
                Map<?, ?> body = JsonUtil.fromRequest(req, Map.class);
                String motivo = (String) body.get("motivo");
                entregaService.rechazarEntrega(idEntrega, userId, motivo);
                JsonUtil.sendSuccess(resp, 200, "Entrega rechazada", null);
            }

        } catch (IllegalArgumentException | IllegalStateException e) {
            JsonUtil.sendError(resp, 400, e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }
    
}
