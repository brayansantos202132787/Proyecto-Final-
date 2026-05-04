/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.controller;

import com.connectwork.service.SaldoService;
import com.connectwork.util.JsonUtil;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

/**
 *
 * @author braya
 */
public class SaldoServlet extends HttpServlet {

    private final SaldoService saldoService = new SaldoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int    userId = (int)    req.getAttribute("userId");
            String role   = (String) req.getAttribute("role");
            String path   = req.getPathInfo();

            if ("/global".equals(path)) {
                if (!"ADMINISTRADOR".equals(role)) {
                    JsonUtil.sendError(resp, 403, "No autorizado");
                    return;
                }
                BigDecimal total = saldoService.obtenerSaldoGlobal();
                JsonUtil.sendSuccess(resp, 200, "OK", Map.of("saldoGlobal", total));

            } else if ("/recargas".equals(path)) {
                JsonUtil.sendSuccess(resp, 200, "OK", saldoService.historialRecargas(userId));

            } else {
                JsonUtil.sendSuccess(resp, 200, "OK", saldoService.obtenerSaldo(userId));
            }
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int    userId = (int)    req.getAttribute("userId");
            String role   = (String) req.getAttribute("role");
            String path   = req.getPathInfo();

            if (!"/recargar".equals(path)) {
                JsonUtil.sendError(resp, 404, "Ruta no encontrada");
                return;
            }
            if (!"CLIENTE".equals(role)) {
                JsonUtil.sendError(resp, 403, "Solo clientes pueden recargar saldo");
                return;
            }

            Map<?,?> body = JsonUtil.fromRequest(req, Map.class);
            double   monto = ((Number) body.get("monto")).doubleValue();

            saldoService.recargar(userId, BigDecimal.valueOf(monto));
            JsonUtil.sendSuccess(resp, 200, "Recarga exitosa", null);

        } catch (IllegalArgumentException e) {
            JsonUtil.sendError(resp, 400, e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }
}
