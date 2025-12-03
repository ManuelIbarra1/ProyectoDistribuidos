/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.profeco.auth.controller;

import com.profeco.auth.model.Usuario;
import com.profeco.auth.service.AuthService;
import com.profeco.auth.security.JwtUtil; // ← Añadir esta importación
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Date;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/test")
    public Map<String, String> test() {
        return Map.of(
            "service", "auth-service",
            "status", "OK", 
            "port", "8081",
            "time", new Date().toString()
        );
    }
    
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "auth-service");
    }
    
    
    @GetMapping("/usuario")
    public ResponseEntity<?> obtenerUsuario(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(Map.of("error", "Token no proporcionado"));
            }
            
            String token = authHeader.substring(7);
            
            
            String username = jwtUtil.obtenerUsername(token);
            String rol = jwtUtil.obtenerRol(token);
            
            Map<String, String> response = new HashMap<>();
            response.put("username", username);
            response.put("rol", rol);
            response.put("fechaLogin", new Date().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Token inválido"));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String token = authService.login(request.getUsuario(), request.getContrasena());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("usuario", request.getUsuario());
            response.put("rol", authService.obtenerRolDesdeToken(token));
            response.put("mensaje", "Login exitoso");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody RegistroRequest request) {
        try {
            String rol = request.getRol() != null ? request.getRol() : "consumidor";
            
            Usuario usuario = authService.registrarUsuario(
                request.getUsuario(), 
                request.getContrasena(), 
                rol
            );
            
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Usuario registrado exitosamente");
            response.put("usuario", usuario.getUsername());
            response.put("rol", usuario.getRol());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/validar")
    public ResponseEntity<?> validarToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new RuntimeException("Token no proporcionado");
            }
            
            String token = authHeader.substring(7);
            boolean esValido = authService.validarToken(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("valido", esValido);
            
            if (esValido) {
                
                String username = jwtUtil.obtenerUsername(token); //
                String rol = jwtUtil.obtenerRol(token); //
                response.put("usuario", username);
                response.put("rol", rol);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Clases internas para requests
    public static class LoginRequest {
        private String usuario;
        private String contrasena;
        
        public String getUsuario() { return usuario; }
        public void setUsuario(String usuario) { this.usuario = usuario; }
        public String getContrasena() { return contrasena; }
        public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    }
    
    public static class RegistroRequest {
        private String usuario;
        private String contrasena;
        private String rol;
        
        public String getUsuario() { return usuario; }
        public void setUsuario(String usuario) { this.usuario = usuario; }
        public String getContrasena() { return contrasena; }
        public void setContrasena(String contrasena) { this.contrasena = contrasena; }
        public String getRol() { return rol; }
        public void setRol(String rol) { this.rol = rol; }
    }
}
