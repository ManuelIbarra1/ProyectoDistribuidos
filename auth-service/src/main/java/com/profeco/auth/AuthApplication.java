/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.profeco.auth;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class AuthApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(AuthApplication.class)
            .properties(
                "server.port=8081",
                "spring.datasource.url=jdbc:h2:mem:authdb", // Base de datos en memoria
                "spring.jpa.hibernate.ddl-auto=create",   // Crear esquema al iniciar
                "spring.h2.console.enabled=true",         // Habilitar consola H2
                "spring.h2.console.path=/h2-console"
            )
            .run(args);
    }
    
    
    
}
