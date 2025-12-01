/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package security;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    
    /**
     * Cifra una contraseña usando BCrypt
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }
    
    /**
     * Verifica si una contraseña plain coincide con el hash cifrado
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            System.err.println("Error verificando contraseña: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica si un password necesita ser recifrado (cuando se cambia el algoritmo)
     */
    public static boolean needsRehash(String hashedPassword) {
        return hashedPassword != null && !hashedPassword.startsWith("$2a$");
    }
}