/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.profeco.auth.security;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {
    
    public String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }
    
    public boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}