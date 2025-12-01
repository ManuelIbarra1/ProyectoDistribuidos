/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

//import entity.User;
//import security.PasswordUtil;
//import jakarta.enterprise.context.ApplicationScoped;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.persistence.TypedQuery;
//import jakarta.transaction.Transactional;
//import java.util.List;
//
//@ApplicationScoped
//public class UsuarioService {
//    
//    @PersistenceContext(unitName = "profecoPU")
//    private EntityManager em;
//    
//    /**
//     * Busca un usuario por su username
//     */
//    public User buscarPorUsername(String username) {
//        try {
//            TypedQuery<User> query = em.createQuery(
//                "SELECT u FROM Usuario u WHERE u.username = :username", User.class);
//            query.setParameter("username", username);
//            return query.getSingleResult();
//        } catch (Exception e) {
//            System.out.println("🔍 Usuario no encontrado: " + username);
//            return null;
//        }
//    }
//    
//    /**
//     * Verifica las credenciales de un usuario
//     */
//    public boolean verificarCredenciales(String username, String password) {
//        User usuario = buscarPorUsername(username);
//        if (usuario == null) {
//            return false;
//        }
//        return PasswordUtil.checkPassword(password, usuario.getPasswordHash());
//    }
//    
//    /**
//     * Obtiene el rol de un usuario
//     */
//    public String obtenerRol(String username) {
//        User usuario = buscarPorUsername(username);
//        return usuario != null ? usuario.getRol() : null;
//    }
//    
//    /**
//     * Crea un nuevo usuario
//     */
//    @Transactional
//    public boolean crearUsuario(String username, String password, String rol) {
//        try {
//            // Verificar si el usuario ya existe
//            if (buscarPorUsername(username) != null) {
//                System.out.println("❌ Usuario ya existe: " + username);
//                return false;
//            }
//            
//            // Cifrar contraseña
//            String passwordHash = PasswordUtil.hashPassword(password);
//            
//            // Crear y guardar usuario
//            User usuario = new User(username, passwordHash, rol);
//            em.persist(usuario);
//            
//            System.out.println("✅ Usuario creado: " + username + " con rol: " + rol);
//            return true;
//            
//        } catch (Exception e) {
//            System.err.println("❌ Error creando usuario: " + e.getMessage());
//            e.printStackTrace();
//            return false;
//        }
//    }
//    
//    /**
//     * Obtiene todos los usuarios
//     */
//    public List<User> obtenerTodos() {
//        TypedQuery<User> query = em.createQuery("SELECT u FROM Usuario u", User.class);
//        return query.getResultList();
//    }
//}