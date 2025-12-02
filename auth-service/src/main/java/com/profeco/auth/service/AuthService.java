/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.profeco.auth.service;

import com.profeco.auth.model.Usuario;
import com.profeco.auth.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import com.profeco.auth.security.JwtUtil;
import com.profeco.auth.security.PasswordUtil;

@Service
public class AuthService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordUtil passwordUtil;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public String login(String username, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        Usuario usuario = usuarioOpt.get();
        
        if (!passwordUtil.checkPassword(password, usuario.getPasswordHash())) {
            throw new RuntimeException("Contraseña incorrecta");
        }
        
        return jwtUtil.generarToken(usuario.getUsername(), usuario.getRol());
    }
    
    public Usuario registrarUsuario(String username, String password, String rol) {
        if (usuarioRepository.existsByUsername(username)) {
            throw new RuntimeException("El usuario ya existe");
        }
        
        String passwordHash = passwordUtil.hashPassword(password);
        Usuario usuario = new Usuario(username, passwordHash, rol);
        
        return usuarioRepository.save(usuario);
    }
    
    public boolean validarToken(String token) {
        return jwtUtil.validarToken(token);
    }
    
    public String obtenerRolDesdeToken(String token) {
        return jwtUtil.obtenerRol(token);
    }
    
    public String obtenerUsernameDesdeToken(String token) {
        return jwtUtil.obtenerUsername(token);
    }
}
