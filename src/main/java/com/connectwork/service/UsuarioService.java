/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.connectwork.service;

import com.connectwork.dao.UsuarioDAO;
import com.connectwork.model.Usuario;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author braya
 */
public class UsuarioService {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public List<Usuario> listarPorTipo(String tipo) throws SQLException {
        return usuarioDAO.findByTipo(tipo);
    }

    public Usuario obtenerPorId(int id) throws SQLException {
        Usuario u = usuarioDAO.findById(id);
        if (u == null) throw new IllegalArgumentException("Usuario no encontrado");
        return u;
    }

    public void toggleEstado(int idUsuario, boolean estado) throws SQLException {
        Usuario u = usuarioDAO.findById(idUsuario);
        if (u == null) throw new IllegalArgumentException("Usuario no encontrado");
        if ("ADMINISTRADOR".equals(u.getTipoUsuario()))
            throw new IllegalArgumentException("No se puede desactivar a un administrador");
        usuarioDAO.updateEstado(idUsuario, estado);
    }

    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> clientes    = usuarioDAO.findByTipo("CLIENTE");
        List<Usuario> freelancers = usuarioDAO.findByTipo("FREELANCER");
        clientes.addAll(freelancers);
        
        clientes.forEach(u -> u.setPasswordHash(null));
        return clientes;
    }
}
