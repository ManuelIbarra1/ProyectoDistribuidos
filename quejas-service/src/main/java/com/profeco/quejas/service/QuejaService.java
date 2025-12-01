/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.profeco.quejas.service;
import com.profeco.quejas.config.RabbitConfig;
import com.profeco.quejas.model.Queja;
import com.profeco.quejas.repository.QuejaRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class QuejaService {
    
    @Autowired
    private QuejaRepository quejaRepository;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private AuthService authService;
    
    public Queja crearQueja(Queja queja, String token) {
        System.out.println("=".repeat(60));
        System.out.println("🔄 [QuejaService] Iniciando creación de queja");
        System.out.println("📋 Título: " + queja.getTitulo());
        System.out.println("👤 Usuario en Body: " + queja.getUsuario());
        System.out.println("🔑 Token recibido: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));

        // Validar token y obtener claims
        System.out.println("🔍 Obteniendo claims desde token...");
        Map<String, Object> claims = authService.obtenerClaimsDesdeToken(token);
        
        if (claims == null) {
            System.out.println("❌ ERROR: Claims son NULL. Token inválido o problema de comunicación.");
            throw new RuntimeException("Token inválido o error de autenticación.");
        }

        String rol = (String) claims.get("rol");
        String usuario = (String) claims.get("usuario");
        
        System.out.println("🎭 Rol obtenido del token: '" + rol + "'");
        System.out.println("👤 Usuario obtenido del token: '" + usuario + "'");

        if (!"consumidor".equals(rol)) {
            System.out.println("❌ ERROR: Rol '" + rol + "' no es 'consumidor'");
            throw new RuntimeException("Solo los consumidores pueden crear quejas");
        }
        
        if (usuario == null || usuario.isEmpty()) {
            System.out.println("❌ ERROR: No se pudo obtener el nombre de usuario del token.");
            throw new RuntimeException("Identidad de usuario no encontrada en el token.");
        }

        System.out.println("✅ Rol validado y usuario extraído del token.");
        
        // Forzar el usuario del token para evitar suplantación
        System.out.println("🔐 Forzando usuario del token en la queja...");
        queja.setUsuario(usuario);
        
        System.out.println("💾 Guardando queja en base de datos para el usuario: " + queja.getUsuario());
        Queja quejaGuardada = quejaRepository.save(queja);
        System.out.println("✅ Queja guardada con ID: " + quejaGuardada.getQuejaId());
        
        // Enviar notificación a RabbitMQ
        System.out.println("📤 Enviando notificación a RabbitMQ...");
        enviarNotificacionRabbitMQ(quejaGuardada);
        
        System.out.println("✅ Queja creada exitosamente");
        System.out.println("=".repeat(60));
        return quejaGuardada;
    }
    
    public List<Queja> obtenerTodasLasQuejas(String token) {
        System.out.println("=".repeat(60));
        System.out.println("🔍 [QuejaService] Obteniendo todas las quejas");
        
        // Validar que sea PROFECO
        String rol = authService.obtenerRolDesdeToken(token);
        System.out.println("🎭 Rol obtenido: '" + rol + "'");
        
        if (rol == null || !"profeco".equals(rol)) {
            System.out.println("❌ ERROR: Acceso denegado. Rol: " + rol);
            throw new RuntimeException("Solo PROFECO puede ver todas las quejas");
        }
        
        System.out.println("✅ Acceso autorizado para PROFECO");
        List<Queja> quejas = quejaRepository.findAllByOrderByFechaDesc();
        System.out.println("📊 Total de quejas encontradas: " + quejas.size());
        System.out.println("=".repeat(60));
        return quejas;
    }
    
    public List<Queja> obtenerQuejasPorUsuario(String usuario, String token) {
        System.out.println("=".repeat(60));
        System.out.println("🔍 [QuejaService] Obteniendo quejas para usuario: " + usuario);
        
        // Validar que el token pertenezca al usuario
        String rol = authService.obtenerRolDesdeToken(token);
        System.out.println("🎭 Rol obtenido: '" + rol + "'");
        
        if (rol == null) {
            System.out.println("❌ ERROR: Token inválido");
            throw new RuntimeException("Token inválido");
        }
        
        // PROFECO puede ver todas, consumidores solo las suyas
        List<Queja> quejas;
        if ("profeco".equals(rol)) {
            System.out.println("✅ Usuario es PROFECO - mostrando todas las quejas");
            quejas = quejaRepository.findAllByOrderByFechaDesc();
        } else {
            System.out.println("✅ Usuario es CONSUMIDOR - mostrando solo sus quejas");
            quejas = quejaRepository.findByUsuarioOrderByFechaDesc(usuario);
        }
        
        System.out.println("📊 Quejas encontradas: " + quejas.size());
        System.out.println("=".repeat(60));
        return quejas;
    }
    
    public Optional<Queja> obtenerQuejaPorId(String quejaId, String token) {
        System.out.println("=".repeat(60));
        System.out.println("🔍 [QuejaService] Obteniendo queja por ID: " + quejaId);
        
        if (!authService.validarToken(token)) {
            System.out.println("❌ ERROR: Token inválido");
            throw new RuntimeException("Token inválido");
        }
        
        System.out.println("✅ Token validado");
        Optional<Queja> queja = quejaRepository.findByQuejaId(quejaId);
        System.out.println("📊 Queja encontrada: " + queja.isPresent());
        System.out.println("=".repeat(60));
        return queja;
    }
    
   private void enviarNotificacionRabbitMQ(Queja queja) {
    try {
        // Crear objeto COMPLETO de notificación
        Map<String, Object> notificacion = new HashMap<>();
        notificacion.put("tipo", "NUEVA_QUEJA");
        notificacion.put("quejaId", queja.getQuejaId());
        notificacion.put("usuario", queja.getUsuario());
        notificacion.put("titulo", queja.getTitulo());
        notificacion.put("comercio", queja.getComercio());
        notificacion.put("fecha", queja.getFecha() != null ? queja.getFecha().toString() : LocalDateTime.now().toString());
        notificacion.put("mensaje", "Nueva queja registrada en el sistema");
        
        // Agregar los campos que Notification Service espera
        notificacion.put("destinatario", "profeco@notificaciones.gob.mx");  // Email de PROFECO
        notificacion.put("asunto", "Nueva Queja Registrada - " + queja.getQuejaId());
        notificacion.put("timestamp", LocalDateTime.now().toString());
        
        System.out.println("🐇 Enviando notificación COMPLETA a RabbitMQ...");
        System.out.println("📨 Contenido: " + notificacion);
        
        rabbitTemplate.convertAndSend(
            RabbitConfig.QUEUE_NOTIFICACIONES, 
            notificacion
        );
        
    } catch (Exception e) {
        System.err.println("❌ Error enviando notificación: " + e.getMessage());
    }
}
    
    public long obtenerTotalQuejas() {
        long total = quejaRepository.count();
        System.out.println("📊 [QuejaService] Total de quejas en sistema: " + total);
        return total;
    }

    public Queja actualizarQueja(String quejaId, Map<String, Object> updates, String token) {
        System.out.println("=".repeat(60));
        System.out.println("🔄 [QuejaService] Actualizando queja: " + quejaId);

        Map<String, Object> claims = authService.obtenerClaimsDesdeToken(token);
        if (claims == null) {
            throw new RuntimeException("Token inválido o error de autenticación.");
        }
        String rol = (String) claims.get("rol");
        String usuario = (String) claims.get("usuario");

        Optional<Queja> quejaOpt = quejaRepository.findByQuejaId(quejaId);
        if (!quejaOpt.isPresent()) {
            throw new RuntimeException("Queja no encontrada con ID: " + quejaId);
        }
        Queja queja = quejaOpt.get();

        if ("profeco".equals(rol)) {
            // PROFECO puede actualizar el estado
            if (updates.containsKey("estado")) {
                String nuevoEstado = (String) updates.get("estado");
                if (nuevoEstado != null && !nuevoEstado.trim().isEmpty()) {
                    System.out.println("📝 (PROFECO) Estado actual: " + queja.getEstado() + " -> Nuevo estado: " + nuevoEstado);
                    queja.setEstado(nuevoEstado);
                }
            }
        } else if ("consumidor".equals(rol)) {
            // Consumidor puede editar título y descripción si es suya y está en estado 'Enviada'
            if (!queja.getUsuario().equals(usuario)) {
                throw new RuntimeException("No tiene permiso para editar esta queja.");
            }
            if (!"Enviada".equalsIgnoreCase(queja.getEstado())) {
                throw new RuntimeException("No se puede editar una queja que ya ha sido procesada.");
            }

            if (updates.containsKey("titulo")) {
                queja.setTitulo((String) updates.get("titulo"));
            }
            if (updates.containsKey("descripcion")) {
                queja.setDescripcion((String) updates.get("descripcion"));
            }
             if (updates.containsKey("estado")) {
                throw new RuntimeException("Un consumidor no puede cambiar el estado de la queja.");
            }
        } else {
            throw new RuntimeException("Rol no autorizado para realizar esta acción.");
        }
        
        Queja quejaActualizada = quejaRepository.save(queja);
        System.out.println("✅ Queja actualizada exitosamente.");
        System.out.println("=".repeat(60));
        
        return quejaActualizada;
    }

    public void eliminarQueja(String quejaId, String token) {
        System.out.println("=".repeat(60));
        System.out.println("🗑️ [QuejaService] Eliminando queja: " + quejaId);
        
        Map<String, Object> claims = authService.obtenerClaimsDesdeToken(token);
        if (claims == null) {
            throw new RuntimeException("Token inválido o error de autenticación.");
        }
        String rol = (String) claims.get("rol");
        String usuario = (String) claims.get("usuario");

        Optional<Queja> quejaOpt = quejaRepository.findByQuejaId(quejaId);
        if (!quejaOpt.isPresent()) {
            throw new RuntimeException("Queja no encontrada con ID: " + quejaId);
        }
        Queja queja = quejaOpt.get();

        boolean puedeEliminar = false;
        if ("profeco".equals(rol)) {
            puedeEliminar = true;
        } else if ("consumidor".equals(rol) && queja.getUsuario().equals(usuario)) {
            puedeEliminar = true;
        }

        if (puedeEliminar) {
            quejaRepository.delete(queja);
            System.out.println("✅ Queja eliminada exitosamente por usuario: " + usuario);
        } else {
            throw new RuntimeException("No tiene permiso para eliminar esta queja.");
        }
        
        System.out.println("=".repeat(60));
    }
}
