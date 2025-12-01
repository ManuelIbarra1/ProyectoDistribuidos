/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.profeco.auth.config;


import com.profeco.auth.model.Usuario;
import com.profeco.auth.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.profeco.auth.security.PasswordUtil;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordUtil passwordUtil;
    
    @Override
    public void run(String... args) throws Exception {
        // Crear usuarios por defecto si no existen
        if (usuarioRepository.count() == 0) {
            Usuario consumidor = new Usuario(
                "maria@email.com", 
                passwordUtil.hashPassword("password123"), 
                "consumidor"
            );
            
            Usuario profeco = new Usuario(
                "profeco", 
                passwordUtil.hashPassword("abcd"), 
                "profeco"
            );
            
            usuarioRepository.save(consumidor);
            usuarioRepository.save(profeco);
            
            System.out.println("✅ Usuarios por defecto creados:");
            System.out.println("   👤 maria@email.com / password123");
            System.out.println("   🔧 profeco / abcd");
        }
    }
}
