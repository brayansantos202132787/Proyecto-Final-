/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.filter;

import com.connectwork.util.JwtUtil;
import com.connectwork.util.JsonUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Set;

/**
 *
 * @author braya
 */
public class JwtFilter implements Filter {
     private static final Set<String> PUBLIC_PATHS = Set.of(
        "/api/auth/login",
        "/api/auth/register"
    );

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest)  req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI().substring(request.getContextPath().length());

        // Dejar pasar rutas públicas
        if (PUBLIC_PATHS.contains(path)) {
            chain.doFilter(req, res);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            JsonUtil.sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token no proporcionado");
            return;
        }

        String token = authHeader.substring(7);

        try {
            int    userId = JwtUtil.getUserId(token);
            String role   = JwtUtil.getRole(token);

            // Pasar datos al servlet mediante atributos
            request.setAttribute("userId", userId);
            request.setAttribute("role",   role);

            chain.doFilter(req, res);

        } catch (JwtException e) {
            JsonUtil.sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado");
        }
    }
}
