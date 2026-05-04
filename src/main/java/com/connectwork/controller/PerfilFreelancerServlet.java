/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.controller;

import com.connectwork.dao.PerfilFreelancerDAO;
import com.connectwork.model.PerfilFreelancer;
import com.connectwork.util.JsonUtil;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author braya
 */
  public class PerfilFreelancerServlet extends HttpServlet {

    private final PerfilFreelancerDAO perfilFreelancerDAO = new PerfilFreelancerDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int    userId = (int) req.getAttribute("userId");
            String path   = req.getPathInfo();

            int idTarget = (path == null || "/".equals(path))
                           ? userId
                           : Integer.parseInt(path.substring(1));

            PerfilFreelancer p = perfilFreelancerDAO.findById(idTarget);
            if (p == null) JsonUtil.sendSuccess(resp, 200, "Perfil no completado", null);
            else           JsonUtil.sendSuccess(resp, 200, "OK", p);

        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }

    // POST → crear perfil (primera vez)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int    userId = (int)    req.getAttribute("userId");
            String role   = (String) req.getAttribute("role");

            if (!"FREELANCER".equals(role)) {
                JsonUtil.sendError(resp, 403, "Solo freelancers pueden completar este perfil");
                return;
            }
            if (perfilFreelancerDAO.exists(userId)) {
                JsonUtil.sendError(resp, 409, "El perfil ya existe, usa PUT para editarlo");
                return;
            }

            PerfilFreelancer p = JsonUtil.fromRequest(req, PerfilFreelancer.class);
            p.setIdUsuario(userId);

            if (p.getBiografia() == null || p.getNivelExperiencia() == null || p.getTarifaHora() == null)
                throw new IllegalArgumentException("Biografía, nivel de experiencia y tarifa son obligatorios");

            perfilFreelancerDAO.insert(p);

            // Insertar habilidades
            if (p.getHabilidades() != null) {
                for (int idHabilidad : p.getHabilidades()) {
                    perfilFreelancerDAO.insertHabilidad(userId, idHabilidad);
                }
            }

            JsonUtil.sendSuccess(resp, 201, "Perfil creado", p);

        } catch (IllegalArgumentException e) {
            JsonUtil.sendError(resp, 400, e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }

    // PUT → actualizar perfil
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int    userId = (int)    req.getAttribute("userId");
            String role   = (String) req.getAttribute("role");

            if (!"FREELANCER".equals(role)) {
                JsonUtil.sendError(resp, 403, "No autorizado");
                return;
            }

            PerfilFreelancer p = JsonUtil.fromRequest(req, PerfilFreelancer.class);
            p.setIdUsuario(userId);
            perfilFreelancerDAO.update(p);

            // Reemplazar habilidades
            if (p.getHabilidades() != null) {
                perfilFreelancerDAO.deleteHabilidades(userId);
                for (int idHabilidad : p.getHabilidades()) {
                    perfilFreelancerDAO.insertHabilidad(userId, idHabilidad);
                }
            }

            JsonUtil.sendSuccess(resp, 200, "Perfil actualizado", p);

        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Error interno");
        }
    }
  }
