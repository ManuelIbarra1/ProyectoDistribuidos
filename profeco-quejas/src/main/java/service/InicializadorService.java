/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;
//import jakarta.annotation.PostConstruct;
//import jakarta.ejb.Singleton;
//import jakarta.ejb.Startup;
//import jakarta.inject.Inject;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import security.PasswordUtil;
///**
// *
// * @author Carlo
// */
//@Singleton
//@Startup
//public class InicializadorService {
//@PersistenceContext(unitName = "profecoPU")
//    private EntityManager em;
//    
//    @PostConstruct
//    public void inicializar() {
//        System.out.println("🚀 Inicializando base de datos PROFECO...");
//        
//        try {
//            // Verificar si ya existen usuarios
//            Long count = (Long) em.createQuery("SELECT COUNT(u) FROM Usuario u").getSingleResult();
//            
//            if (count == 0) {
//                System.out.println("📝 Creando usuarios por defecto...");
//                crearUsuariosPorDefecto();
//            } else {
//                System.out.println("✅ Base de datos ya tiene " + count + " usuarios");
//            }
//            
//        } catch (Exception e) {
//            System.err.println("❌ Error inicializando base de datos: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//    
//    private void crearUsuariosPorDefecto() {
//        try {
//            // Usuario consumidor
//            String hashConsumidor = PasswordUtil.hashPassword("1234");
//            entity.User consumidor = new entity.User("consumidor", hashConsumidor, "consumidor");
//            em.persist(consumidor);
//            
//            // Usuario profeco
//            String hashProfeco = PasswordUtil.hashPassword("abcd");
//            entity.User profeco = new entity.User("profeco", hashProfeco, "profeco");
//            em.persist(profeco);
//            
//            // Usuarios de prueba adicionales
//            String hashMaria = PasswordUtil.hashPassword("clave123");
//            entity.User maria = new entity.User("maria", hashMaria, "consumidor");
//            em.persist(maria);
//            
//            String hashCarlos = PasswordUtil.hashPassword("password456");
//            entity.User carlos = new entity.User("carlos", hashCarlos, "consumidor");
//            em.persist(carlos);
//            
//            System.out.println("✅ Usuarios por defecto creados:");
//            System.out.println("   👤 consumidor / 1234");
//            System.out.println("   🔧 profeco / abcd");
//            System.out.println("   👤 maria / clave123");
//            System.out.println("   👤 carlos / password456");
//            
//        } catch (Exception e) {
//            System.err.println("❌ Error creando usuarios por defecto: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}
