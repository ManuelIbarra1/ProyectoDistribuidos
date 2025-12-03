package com.profeco.quejas.controller;

import com.profeco.quejas.dto.QuejaRequest;
import com.profeco.quejas.model.Queja;
import com.profeco.quejas.service.QuejaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quejas")
//@CrossOrigin(origins = "*")
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
    public ResponseEntity<?> crearQueja(@RequestBody QuejaRequest quejaRequest, 
                                       @RequestHeader("Authorization") String authHeader) {
        try {
            System.out.println("📝 [QuejaController] Recibiendo nueva queja...");
            System.out.println("📋 Título: " + quejaRequest.getTitulo());
            System.out.println("🏪 Comercio: " + quejaRequest.getComercio());
            
            String token = extraerToken(authHeader);
            
            // Crear entidad Queja a partir del DTO
            Queja queja = new Queja();
            queja.setTitulo(quejaRequest.getTitulo());
            queja.setDescripcion(quejaRequest.getDescripcion());
            queja.setComercio(quejaRequest.getComercio());
            // NOTA: El usuario se asignará automáticamente desde el token en el servicio
            
            Queja quejaCreada = quejaService.crearQueja(queja, token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", quejaCreada.getQuejaId());
            response.put("mensaje", "Queja registrada exitosamente");
            response.put("estado", quejaCreada.getEstado());
            response.put("notificacion", "enviada a RabbitMQ");
            response.put("fecha", quejaCreada.getFecha());
            response.put("usuario", quejaCreada.getUsuario());
            
            System.out.println("✅ [QuejaController] Queja creada: " + quejaCreada.getQuejaId());
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            System.err.println("❌ [QuejaController] Error: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping
    public ResponseEntity<?> obtenerTodasLasQuejas(@RequestHeader("Authorization") String authHeader) {
        try {
            System.out.println("📊 [QuejaController] Obteniendo todas las quejas...");
            String token = extraerToken(authHeader);
            List<Queja> quejas = quejaService.obtenerTodasLasQuejas(token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("total", quejas.size());
            response.put("quejas", quejas);
            
            System.out.println("✅ [QuejaController] Encontradas " + quejas.size() + " quejas");
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            System.err.println("❌ [QuejaController] Error obteniendo quejas: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(403).body(error); // 403 Forbidden para acceso denegado
        }
    }
    
    @GetMapping("/usuario/{usuario}")
    public ResponseEntity<?> obtenerQuejasPorUsuario(@PathVariable String usuario,
                                                   @RequestHeader("Authorization") String authHeader) {
        try {
            System.out.println("👤 [QuejaController] Obteniendo quejas para usuario: " + usuario);
            String token = extraerToken(authHeader);
            List<Queja> quejas = quejaService.obtenerQuejasPorUsuario(usuario, token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("usuario", usuario);
            response.put("total", quejas.size());
            response.put("quejas", quejas);
            
            System.out.println("✅ [QuejaController] Encontradas " + quejas.size() + " quejas para " + usuario);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            System.err.println("❌ [QuejaController] Error obteniendo quejas de usuario: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/mis-quejas")
    public ResponseEntity<?> obtenerMisQuejas(@RequestHeader("Authorization") String authHeader) {
        try {
            System.out.println("👤 [QuejaController] Obteniendo mis quejas...");
            String token = extraerToken(authHeader);
            
            // Obtener usuario del token
            String usuario = quejaService.obtenerUsuarioDesdeToken(token);
            System.out.println("👤 Usuario autenticado: " + usuario);
            
            List<Queja> quejas = quejaService.obtenerQuejasPorUsuario(usuario, token);
            
            Map<String, Object> response = new HashMap<>();
            response.put("usuario", usuario);
            response.put("total", quejas.size());
            response.put("quejas", quejas);
            
            System.out.println("✅ [QuejaController] Encontradas " + quejas.size() + " quejas propias");
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            System.err.println("❌ [QuejaController] Error obteniendo mis quejas: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/{quejaId}")
    public ResponseEntity<?> obtenerQuejaPorId(@PathVariable String quejaId,
                                             @RequestHeader("Authorization") String authHeader) {
        try {
            System.out.println("🔍 [QuejaController] Obteniendo queja ID: " + quejaId);
            String token = extraerToken(authHeader);
            
            return quejaService.obtenerQuejaPorId(quejaId, token)
                    .map(queja -> {
                        System.out.println("✅ [QuejaController] Queja encontrada: " + quejaId);
                        return ResponseEntity.ok(queja);
                    })
                    .orElseGet(() -> {
                        System.out.println("❌ [QuejaController] Queja no encontrada: " + quejaId);
                        return ResponseEntity.notFound().build();
                    });
                    
        } catch (RuntimeException e) {
            System.err.println("❌ [QuejaController] Error obteniendo queja: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/estadisticas/total")
    public ResponseEntity<?> obtenerTotalQuejas(@RequestHeader("Authorization") String authHeader) {
        try {
            System.out.println("📈 [QuejaController] Obteniendo estadísticas...");
            String token = extraerToken(authHeader);
            
            // Verificar si es PROFECO
            String rol = quejaService.obtenerRolDesdeToken(token);
            System.out.println("🎭 Rol del usuario: " + rol);
            
            if (!"profeco".equals(rol)) {
                throw new RuntimeException("Solo PROFECO puede ver estadísticas");
            }
            
            long total = quejaService.obtenerTotalQuejas();
            
            Map<String, Object> response = new HashMap<>();
            response.put("totalQuejas", total);
            response.put("fechaConsulta", new Date().toString());
            
            System.out.println("✅ [QuejaController] Total de quejas: " + total);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            System.err.println("❌ [QuejaController] Error obteniendo estadísticas: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(403).body(error);
        }
    }
    
    @GetMapping("/estadisticas/resumen")
    public ResponseEntity<?> obtenerResumenEstadisticas(@RequestHeader("Authorization") String authHeader) {
        try {
            System.out.println("📊 [QuejaController] Obteniendo resumen estadístico...");
            String token = extraerToken(authHeader);
            
            // Verificar si es PROFECO
            String rol = quejaService.obtenerRolDesdeToken(token);
            if (!"profeco".equals(rol)) {
                throw new RuntimeException("Solo PROFECO puede ver estadísticas");
            }
            
            long total = quejaService.obtenerTotalQuejas();
            List<Queja> todasLasQuejas = quejaService.obtenerTodasLasQuejas(token);
            
            // Calcular estadísticas
            long recibidas = todasLasQuejas.stream()
                .filter(q -> "RECIBIDA".equals(q.getEstado()))
                .count();
            
            long enProceso = todasLasQuejas.stream()
                .filter(q -> "EN_PROCESO".equals(q.getEstado()))
                .count();
            
            long resueltas = todasLasQuejas.stream()
                .filter(q -> "RESUELTA".equals(q.getEstado()))
                .count();
            
            Map<String, Object> response = new HashMap<>();
            response.put("totalQuejas", total);
            response.put("recibidas", recibidas);
            response.put("enProceso", enProceso);
            response.put("resueltas", resueltas);
            response.put("fechaConsulta", new Date().toString());
            
            System.out.println("✅ [QuejaController] Resumen generado");
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            System.err.println("❌ [QuejaController] Error en resumen: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(403).body(error);
        }
    }
    
    private String extraerToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token de autorización requerido");
        }
        return authHeader.substring(7);
    }
}