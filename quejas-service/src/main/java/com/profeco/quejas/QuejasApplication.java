/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.profeco.quejas;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class QuejasApplication {
    public static void main(String[] args) {
    new SpringApplicationBuilder(QuejasApplication.class)
        .properties(
            "server.port=8082",
            "spring.datasource.url=jdbc:h2:mem:quejasdb",
            "spring.jpa.hibernate.ddl-auto=update", // O 'create'
            "spring.rabbitmq.host=localhost",
            // IMPORTANTE: Esta URL debe tener /api
            "auth.service.url=http://localhost:8081/api"  // ← CON /api
        )
        .run(args);
}
}
