/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messaging;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Channel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
/**
 *
 * @author Carlo
 */
@ApplicationScoped
public class RabbitMQService {
   private Connection connection;
    private final static String QUEUE_NOTIFICACIONES = "profeco.notificaciones";
    
    @PostConstruct
    public void init() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            factory.setUsername("guest");
            factory.setPassword("guest");
            factory.setPort(5672);
            
            this.connection = factory.newConnection();
            System.out.println("✅ Conectado a RabbitMQ en localhost:5672");
            
            // Crear la cola si no existe
            try (Channel channel = connection.createChannel()) {
                channel.queueDeclare(QUEUE_NOTIFICACIONES, true, false, false, null);
                System.out.println("✅ Cola creada: " + QUEUE_NOTIFICACIONES);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error conectando a RabbitMQ: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void enviarMensaje(String mensaje) {
        if (connection == null) {
            System.err.println("❌ No hay conexión a RabbitMQ");
            return;
        }
        
        try (Channel channel = connection.createChannel()) {
            channel.basicPublish("", QUEUE_NOTIFICACIONES, null, mensaje.getBytes());
            System.out.println("📤 Mensaje enviado a RabbitMQ: " + mensaje);
        } catch (Exception e) {
            System.err.println("❌ Error enviando mensaje a RabbitMQ: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @PreDestroy
    public void cleanup() {
        try {
            if (connection != null && connection.isOpen()) {
                connection.close();
                System.out.println("🔌 Conexión RabbitMQ cerrada");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
