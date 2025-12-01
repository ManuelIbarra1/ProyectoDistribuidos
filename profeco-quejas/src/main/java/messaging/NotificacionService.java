/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messaging;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Notificacion;

/**
 *
 * @author Carlo
 */
@ApplicationScoped
public class NotificacionService {
 @Inject
    private RabbitMQService rabbitService;
    
    private final ObjectMapper mapper = new ObjectMapper();
    
    public void enviarNotificacionQueja(String usuario, String quejaId, String tituloQueja) {
        try {
            Notificacion notificacion = new Notificacion(
                usuario, 
                "Queja registrada: " + tituloQueja,
                "Hemos recibido tu queja con ID: " + quejaId + ". Está siendo procesada por PROFECO.",
                quejaId,
                "QUEJA_RECIBIDA",
                java.time.LocalDateTime.now().toString()
            );
            
            String mensajeJson = mapper.writeValueAsString(notificacion);
            rabbitService.enviarMensaje(mensajeJson);
            
        } catch (Exception e) {
            System.err.println("❌ Error serializando/enviando notificación: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
