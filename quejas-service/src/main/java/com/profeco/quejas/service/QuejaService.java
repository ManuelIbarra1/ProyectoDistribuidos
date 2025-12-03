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
        System.out.println(" [QuejaService] Iniciando creación de queja");

        // 1. Validar token
        if (!authService.validarToken(token)) {
            throw new RuntimeException("Token inválido o expirado");
        }

        // 2. Obtener usuario REAL del token (IMPORTANTE: evita spoofing)
        String usuarioReal = authService.obtenerUsuarioDesdeToken(token);
        System.out.println(" Usuario obtenido del token: '" + usuarioReal + "'");

        if (usuarioReal == null || usuarioReal.isEmpty()) {
            throw new RuntimeException("No se pudo obtener usuario del token");
        }

        // 3. Asignar usuario REAL (sobrescribe cualquier usuario en la queja)
        queja.setUsuario(usuarioReal);
        System.out.println(" Usuario asignado a queja: " + usuarioReal);

        // 4. Validar que sea consumidor
        String rol = authService.obtenerRolDesdeToken(token);
        System.out.println(" Rol obtenido: '" + rol + "'");

        if (!"consumidor".equals(rol)) {
            throw new RuntimeException("Solo los consumidores pueden crear quejas");
        }

        System.out.println(" Usuario es consumidor - autorizado");

        // 5. Validar campos requeridos
        if (queja.getTitulo() == null || queja.getTitulo().trim().isEmpty()) {
            throw new RuntimeException("El título es obligatorio");
        }

        if (queja.getDescripcion() == null || queja.getDescripcion().trim().isEmpty()) {
            throw new RuntimeException("La descripción es obligatoria");
        }

        if (queja.getComercio() == null || queja.getComercio().trim().isEmpty()) {
            throw new RuntimeException("El comercio es obligatorio");
        }

        // 6. Guardar queja
        System.out.println(" Guardando queja en base de datos...");
        Queja quejaGuardada = quejaRepository.save(queja);
        System.out.println(" Queja guardada con ID: " + quejaGuardada.getQuejaId());

        // 7. Enviar notificación a RabbitMQ
        try {
            System.out.println(" Enviando notificación a RabbitMQ...");
            enviarNotificacionRabbitMQ(quejaGuardada);
            System.out.println(" Notificación enviada a RabbitMQ");
        } catch (Exception e) {
            System.err.println("️  Error enviando a RabbitMQ: " + e.getMessage());
            
        }

        System.out.println(" Queja creada exitosamente");
        System.out.println("=".repeat(60));
        return quejaGuardada;
    }

    public List<Queja> obtenerTodasLasQuejas(String token) {
        System.out.println("=".repeat(60));
        System.out.println(" [QuejaService] Obteniendo todas las quejas");

        // Validar que sea PROFECO
        String rol = authService.obtenerRolDesdeToken(token);
        System.out.println(" Rol obtenido: '" + rol + "'");

        if (rol == null || !"profeco".equals(rol)) {
            System.out.println(" ERROR: Acceso denegado. Rol: " + rol);
            throw new RuntimeException("Solo PROFECO puede ver todas las quejas");
        }

        System.out.println(" Acceso autorizado para PROFECO");
        List<Queja> quejas = quejaRepository.findAllByOrderByFechaDesc();
        System.out.println(" Total de quejas encontradas: " + quejas.size());
        System.out.println("=".repeat(60));
        return quejas;
    }

    public List<Queja> obtenerQuejasPorUsuario(String usuario, String token) {
        System.out.println("=".repeat(60));
        System.out.println(" [QuejaService] Obteniendo quejas para usuario: " + usuario);

        // Validar que el token pertenezca al usuario
        String rol = authService.obtenerRolDesdeToken(token);
        System.out.println(" Rol obtenido: '" + rol + "'");

        if (rol == null) {
            System.out.println(" ERROR: Token inválido");
            throw new RuntimeException("Token inválido");
        }

        // PROFECO puede ver todas, consumidores solo las suyas
        List<Queja> quejas;
        if ("profeco".equals(rol)) {
            System.out.println(" Usuario es PROFECO - mostrando todas las quejas");
            quejas = quejaRepository.findAllByOrderByFechaDesc();
        } else {
            System.out.println(" Usuario es CONSUMIDOR - mostrando solo sus quejas");
            quejas = quejaRepository.findByUsuarioOrderByFechaDesc(usuario);
        }

        System.out.println(" Quejas encontradas: " + quejas.size());
        System.out.println("=".repeat(60));
        return quejas;
    }

    public Optional<Queja> obtenerQuejaPorId(String quejaId, String token) {
        System.out.println("=".repeat(60));
        System.out.println(" [QuejaService] Obteniendo queja por ID: " + quejaId);

        if (!authService.validarToken(token)) {
            System.out.println(" ERROR: Token inválido");
            throw new RuntimeException("Token inválido");
        }

        System.out.println(" Token validado");
        Optional<Queja> queja = quejaRepository.findByQuejaId(quejaId);
        System.out.println(" Queja encontrada: " + queja.isPresent());
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

            
            notificacion.put("destinatario", "profeco@notificaciones.gob.mx");
            notificacion.put("asunto", "Nueva Queja Registrada - " + queja.getQuejaId());
            notificacion.put("timestamp", LocalDateTime.now().toString());

            System.out.println(" Enviando notificación COMPLETA a RabbitMQ...");
            System.out.println(" Contenido: " + notificacion);

            rabbitTemplate.convertAndSend(
                    RabbitConfig.QUEUE_NOTIFICACIONES,
                    notificacion
            );

        } catch (Exception e) {
            System.err.println(" Error enviando notificación: " + e.getMessage());
        }
    }

    public long obtenerTotalQuejas() {
        long total = quejaRepository.count();
        System.out.println(" [QuejaService] Total de quejas en sistema: " + total);
        return total;
    }

    // Agrega estos métodos en QuejaService.java
    public String obtenerUsuarioDesdeToken(String token) {
        return authService.obtenerUsuarioDesdeToken(token);
    }

    public String obtenerRolDesdeToken(String token) {
        return authService.obtenerRolDesdeToken(token);
    }

}
