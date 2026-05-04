/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.controller;

import com.connectwork.service.ReporteService;
import com.connectwork.util.JsonUtil;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 *
 * @author braya
 */
public class ReporteServlet extends HttpServlet {
     private final ReporteService reporteService = new ReporteService();
     
     @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int    userId = (int)    req.getAttribute("userId");
            String role   = (String) req.getAttribute("role");
            String path   = req.getPathInfo();           // ej: /top-freelancers
            String desde  = req.getParameter("desde");
            String hasta  = req.getParameter("hasta");

            if (path == null) { JsonUtil.sendError(resp, 400, "Tipo de reporte requerido"); return; }
            String tipo = path.substring(1);

            Object resultado = switch (tipo) {
                // ADMIN
                case "top-freelancers"     -> { checkRole(role, "ADMINISTRADOR"); yield reporteService.topFreelancers(desde, hasta); }
                case "top-categorias"      -> { checkRole(role, "ADMINISTRADOR"); yield reporteService.topCategorias(desde, hasta); }
                case "ingresos-plataforma" -> { checkRole(role, "ADMINISTRADOR"); yield reporteService.ingresosTotales(desde, hasta); }
                case "historial-comisiones"-> { checkRole(role, "ADMINISTRADOR"); yield reporteService.historialComisiones(); }
                // CLIENTE
                case "proyectos-cliente"   -> reporteService.proyectosPorCliente(userId, desde, hasta);
                case "recargas-cliente"    -> reporteService.recargas(userId);
                case "gasto-categoria"     -> reporteService.gastoPorCategoria(userId, desde, hasta);
                // FREELANCER
                case "contratos-freelancer"-> reporteService.contratosFreelancer(userId, desde, hasta);
                case "propuestas-freelancer"-> reporteService.propuestasFreelancer(userId, desde, hasta);
                case "top-cats-freelancer" -> reporteService.topCategoriaFreelancer(userId);
                default -> throw new IllegalArgumentException("Tipo de reporte desconocido");
            };

            JsonUtil.sendSuccess(resp, 200, "OK", resultado);

        } catch (IllegalArgumentException e) {
            JsonUtil.sendError(resp, 400, e.getMessage());
        } catch (SecurityException e) {
            JsonUtil.sendError(resp, 403, e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }

    private void checkRole(String actual, String requerido) {
        if (!requerido.equals(actual))
            throw new SecurityException("Acceso no autorizado");
    }
    
}
