/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.profeco.quejas.controller;

import com.profeco.quejas.model.Queja;
import com.profeco.quejas.service.QuejaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/quejas")
@CrossOrigin(origins = "*")
public class QuejaController {
    
    @Autowired
    private QuejaService quejaService;
    
    @GetMapping("/test")
public Map<String, String> test() {
    return Map.of(
        "service", "quejas-service",
        "status", "OK", 
        "port", "8082",
        "time", new Date().toString()
    );
}

@GetMapping("/health")
public Map<String, String> health() {
    return Map.of("status", "UP", "service", "quejas-service");
}
    
    @PostMapping
    public ResponseEntity<?> crearQueja(@RequestBody Queja queja, 
                                       @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extraerToken(authHeader);
            Queja quejaCreada = quejaService.crearQueja(queja, token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", quejaCreada.getQuejaId());
            response.put("mensaje", "Queja registrada exitosamente");
            response.put("estado", quejaCreada.getEstado());
            response.put("notificacion", "enviada a RabbitMQ");
            response.put("fecha", quejaCreada.getFecha());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping
    public ResponseEntity<?> obtenerTodasLasQuejas(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = extraerToken(authHeader);
            List<Queja> quejas = quejaService.obtenerTodasLasQuejas(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("total", quejas.size());
            response.put("quejas", quejas);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/usuario/{usuario}")
    public ResponseEntity<?> obtenerQuejasPorUsuario(@PathVariable String usuario,
                                                   @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extraerToken(authHeader);
            List<Queja> quejas = quejaService.obtenerQuejasPorUsuario(usuario, token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("usuario", usuario);
            response.put("total", quejas.size());
            response.put("quejas", quejas);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/{quejaId}")
    public ResponseEntity<?> obtenerQuejaPorId(@PathVariable String quejaId,
                                             @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extraerToken(authHeader);
            return quejaService.obtenerQuejaPorId(quejaId, token)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
                    
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/estadisticas/total")
    public ResponseEntity<?> obtenerTotalQuejas(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = extraerToken(authHeader);
            // Solo PROFECO puede ver estadísticas
            String rol = quejaService.obtenerQuejasPorUsuario("dummy", token).isEmpty() ? 
                        "consumidor" : "profeco";
            
            if (!"profeco".equals(rol)) {
                throw new RuntimeException("Solo PROFECO puede ver estadísticas");
            }
            
            long total = quejaService.obtenerTotalQuejas();
            
            Map<String, Object> response = new HashMap<>();
            response.put("totalQuejas", total);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{quejaId}")
    public ResponseEntity<?> actualizarQueja(@PathVariable String quejaId,
                                                 @RequestBody Map<String, Object> updates,
                                                 @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extraerToken(authHeader);
            
            Queja quejaActualizada = quejaService.actualizarQueja(quejaId, updates, token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Queja actualizada exitosamente");
            response.put("queja", quejaActualizada);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/{quejaId}")
    public ResponseEntity<?> eliminarQueja(@PathVariable String quejaId,
                                          @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extraerToken(authHeader);
            quejaService.eliminarQueja(quejaId, token);
            
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Queja eliminada exitosamente");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    private String extraerToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token de autorización requerido");
        }
        return authHeader.substring(7);
    }
}
