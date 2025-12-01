/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.profeco.notification;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
/**
 *
 * @author Carlo
 */
@SpringBootApplication
public class NotificationApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(NotificationApplication.class)
            .properties(
                "server.port=8083",
                "spring.rabbitmq.host=localhost",
                "spring.rabbitmq.port=5672", 
                "spring.rabbitmq.username=guest",
                "spring.rabbitmq.password=guest",
                "logging.level.com.profeco.notification=DEBUG"
            )
            .run(args);
        
        System.out.println("🔔 Notification Service running on port 8083");
        System.out.println("👂 Listening to RabbitMQ queue: profeco.notificaciones");
    }
}