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
        
        System.out.println("🚀 [DataInitializer] Verificando usuarios por defecto (versión robusta)...");
        
        // --- Usuario Consumidor ---
        if (!usuarioRepository.existsByUsername("maria@email.com")) {
            Usuario consumidor = new Usuario(
                "maria@email.com", 
                passwordUtil.hashPassword("password123"), 
                "consumidor"
            );
            usuarioRepository.save(consumidor);
            System.out.println("   ✅ Creado usuario: maria@email.com / password123");
        } else {
            System.out.println("   ℹ️ Usuario 'maria@email.com' ya existe.");
        }
        
        // --- Usuario PROFECO (con verificación y corrección de contraseña) ---
        String profecoUsername = "admin@profeco.gob.mx";
        String profecoPassword = "admin123";
        
        if (!usuarioRepository.existsByUsername(profecoUsername)) {
            Usuario profeco = new Usuario(
                profecoUsername,
                passwordUtil.hashPassword(profecoPassword), 
                "profeco"
            );
            usuarioRepository.save(profeco);
            System.out.println("   ✅ Creado usuario PROFECO: " + profecoUsername);
        } else {
            System.out.println("   ℹ️ Usuario '" + profecoUsername + "' ya existe. Verificando contraseña...");
            Usuario profeco = usuarioRepository.findByUsername(profecoUsername).get();
            if (!passwordUtil.checkPassword(profecoPassword, profeco.getPasswordHash())) {
                System.out.println("   ⚠️ Contraseña de PROFECO incorrecta. ¡Actualizando!");
                profeco.setPasswordHash(passwordUtil.hashPassword(profecoPassword));
                usuarioRepository.save(profeco);
                System.out.println("   ✅ Contraseña de PROFECO actualizada.");
            } else {
                System.out.println("   👍 Contraseña de PROFECO es correcta.");
            }
        }
        
        System.out.println("🏁 [DataInitializer] Verificación completa.");
    }
}
