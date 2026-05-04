/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.service;

import com.connectwork.dao.UsuarioDAO;
import com.connectwork.dao.SaldoDAO;
import com.connectwork.model.Usuario;
import com.connectwork.util.JwtUtil;
import com.connectwork.util.PasswordUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author braya
 */
public class AuthService {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final SaldoDAO   saldoDAO   = new SaldoDAO();

    public Map<String, Object> login(String nombreUsuario, String password) throws SQLException {
        Usuario u = usuarioDAO.findByNombreUsuario(nombreUsuario);

        if (u == null) throw new IllegalArgumentException("Usuario no encontrado");
        if (!u.isEstadoCuenta()) throw new IllegalArgumentException("Cuenta desactivada");
        if (!PasswordUtil.verifyPassword(password, u.getPasswordHash()))
            throw new IllegalArgumentException("Contraseña incorrecta");

        String token = JwtUtil.generateToken(u.getIdUsuario(), u.getTipoUsuario());

        Map<String, Object> resp = new HashMap<>();
        resp.put("token",      token);
        resp.put("idUsuario",  u.getIdUsuario());
        resp.put("tipo",       u.getTipoUsuario());
        resp.put("nombre",     u.getNombreCompleto());
        return resp;
    }

    public int register(Usuario u) throws SQLException {
        if (usuarioDAO.existeNombreUsuario(u.getNombreUsuario()))
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
        if (usuarioDAO.existeCorreo(u.getCorreo()))
            throw new IllegalArgumentException("El correo ya está registrado");

        u.setPasswordHash(PasswordUtil.hashPassword(u.getPasswordHash()));
        int nuevoId = usuarioDAO.insert(u);

        // Crear saldo inicial en 0
        saldoDAO.initSaldo(nuevoId);

        return nuevoId;
    }
}
