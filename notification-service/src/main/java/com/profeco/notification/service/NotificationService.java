/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.profeco.notification.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.profeco.notification.model.Notificacion;

@Service
public class NotificationService {

 private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    
    public NotificationService(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }
    
    public void enviarNotificacionQueja(String usuario, String quejaId, String tituloQueja) {
        try {
            // Crear objeto Notificacion
            Notificacion notificacion = new Notificacion(
                usuario,
                "Queja registrada: " + tituloQueja,
                "Hemos recibido tu queja con ID: " + quejaId + ". Está siendo procesada por PROFECO.",
                quejaId,
                "QUEJA_RECIBIDA"
            );
            
            // Convertir a JSON y enviar
            String mensajeJson = objectMapper.writeValueAsString(notificacion);
            rabbitTemplate.convertAndSend("profeco.notificaciones", mensajeJson);
            
            System.out.println("📤 Notificación JSON enviada a RabbitMQ: " + mensajeJson);
            
        } catch (Exception e) {
            System.err.println("❌ Error enviando notificación JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
