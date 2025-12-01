/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.profeco.auth.controller;

import com.profeco.auth.model.Usuario;
import com.profeco.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Date;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
//@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
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
            // Por defecto, nuevos usuarios son "consumidor"
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
                response.put("usuario", authService.obtenerRolDesdeToken(token));
                response.put("rol", authService.obtenerRolDesdeToken(token));
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
        
        // Getters y Setters
        public String getUsuario() { return usuario; }
        public void setUsuario(String usuario) { this.usuario = usuario; }
        public String getContrasena() { return contrasena; }
        public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    }
    
    public static class RegistroRequest {
        private String usuario;
        private String contrasena;
        private String rol;
        
        // Getters y Setters
        public String getUsuario() { return usuario; }
        public void setUsuario(String usuario) { this.usuario = usuario; }
        public String getContrasena() { return contrasena; }
        public void setContrasena(String contrasena) { this.contrasena = contrasena; }
        public String getRol() { return rol; }
        public void setRol(String rol) { this.rol = rol; }
    }
}
