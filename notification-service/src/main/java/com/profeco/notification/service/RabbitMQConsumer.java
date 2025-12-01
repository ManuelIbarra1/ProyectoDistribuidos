/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.profeco.notification.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.profeco.notification.model.Notificacion;

@Service
public class RabbitMQConsumer {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @RabbitListener(queues = "profeco.notificaciones")
    public void recibirNotificacion(Notificacion notificacion) { // ← Cambiar a recibir el objeto directamente
        try {
            System.out.println("📨 Objeto Notificacion recibido de RabbitMQ:");
            System.out.println("   Destinatario: " + notificacion.getDestinatario());
            System.out.println("   Asunto: " + notificacion.getAsunto());
            System.out.println("   Tipo: " + notificacion.getTipo());
            
            procesarNotificacion(notificacion);
            
        } catch (Exception e) {
            System.err.println("❌ Error procesando notificación: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void procesarNotificacion(Notificacion notificacion) {
        // Simular envío de notificación
        System.out.println("✅ ENVIANDO NOTIFICACIÓN:");
        System.out.println("   📧 Para: " + notificacion.getDestinatario());
        System.out.println("   📝 Asunto: " + notificacion.getAsunto());
        System.out.println("   📄 Mensaje: " + notificacion.getMensaje());
        System.out.println("   🏷️ Tipo: " + notificacion.getTipo());
        System.out.println("   🔗 Queja ID: " + notificacion.getQuejaId());
        System.out.println("   ⏰ Timestamp: " + notificacion.getTimestamp());
        
        notificacion.setEnviada(true);
        System.out.println("🎯 Notificación procesada exitosamente");
    }
}
