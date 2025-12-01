/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package auth;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import security.JwtUtil;
import security.PasswordUtil;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author Carlo
 */
@Path("/auth")
public class LoginResource {

   public static class LoginRequest {
        public String usuario;
        public String contrasena;
    }

    public static class LoginResponse {
        public String token;
        public String usuario;
        public String rol;

        public LoginResponse(String token, String usuario, String rol) {
            this.token = token;
            this.usuario = usuario;
            this.rol = rol;
        }
    }

    // 🔐 Base de datos en memoria CON CONTRASEÑAS CIFRADAS
    private static final Map<String, UsuarioEnMemoria> usuarios = new HashMap<>();
    
    static {
        // Inicializar usuarios con contraseñas CIFRADAS
        usuarios.put("consumidor", new UsuarioEnMemoria(
            "consumidor", 
            PasswordUtil.hashPassword("1234"), // ← Contraseña cifrada
            "consumidor"
        ));
        
        usuarios.put("profeco", new UsuarioEnMemoria(
            "profeco", 
            PasswordUtil.hashPassword("abcd"), // ← Contraseña cifrada  
            "profeco"
        ));
        
        usuarios.put("maria", new UsuarioEnMemoria(
            "maria",
            PasswordUtil.hashPassword("clave123"),
            "consumidor"
        ));
        
        System.out.println("✅ Usuarios inicializados con BCrypt");
        System.out.println("   👤 consumidor / 1234");
        System.out.println("   🔧 profeco / abcd");
        System.out.println("   👤 maria / clave123");
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest req) {
        System.out.println("🎯 Intento de login: " + req.usuario);

        try {
            if (req.usuario == null || req.contrasena == null) {
                return Response.status(400).entity("{\"error\": \"Datos incompletos\"}").build();
            }

            // 🔐 Buscar usuario en memoria
            UsuarioEnMemoria usuario = usuarios.get(req.usuario);
            
            if (usuario == null) {
                System.out.println("❌ Usuario no encontrado: " + req.usuario);
                return Response.status(401).entity("{\"error\": \"Usuario o contraseña incorrecta\"}").build();
            }

            // 🔐 Verificar contraseña CIFRADA
            boolean passwordValido = PasswordUtil.checkPassword(req.contrasena, usuario.passwordHash);
            
            if (!passwordValido) {
                System.out.println("❌ Contraseña incorrecta para: " + req.usuario);
                return Response.status(401).entity("{\"error\": \"Usuario o contraseña incorrecta\"}").build();
            }

            System.out.println("✅ Login exitoso - Usuario: " + req.usuario + ", Rol: " + usuario.rol);

            // Generar token JWT
            String token = JwtUtil.generarToken(req.usuario, usuario.rol);

            return Response.ok(new LoginResponse(token, req.usuario, usuario.rol)).build();

        } catch (Exception e) {
            System.err.println("💥 Error en login: " + e.getMessage());
            e.printStackTrace();
            return Response.status(500).entity("{\"error\": \"Error interno del servidor\"}").build();
        }
    }
    
    @POST
    @Path("/registro")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarUsuario(LoginRequest req) {
        try {
            if (req.usuario == null || req.contrasena == null) {
                return Response.status(400).entity("{\"error\": \"Datos incompletos\"}").build();
            }
            
            if (usuarios.containsKey(req.usuario)) {
                return Response.status(400).entity("{\"error\": \"El usuario ya existe\"}").build();
            }
            
            // 🔐 Cifrar contraseña antes de guardar
            String passwordHash = PasswordUtil.hashPassword(req.contrasena);
            usuarios.put(req.usuario, new UsuarioEnMemoria(req.usuario, passwordHash, "consumidor"));
            
            System.out.println("✅ Nuevo usuario registrado: " + req.usuario);
            
            return Response.ok("{\"mensaje\": \"Usuario registrado exitosamente\"}").build();
            
        } catch (Exception e) {
            return Response.status(500).entity("{\"error\": \"Error registrando usuario\"}").build();
        }
    }
    
    // Clase interna para usuarios en memoria
    private static class UsuarioEnMemoria {
        public String username;
        public String passwordHash;
        public String rol;
        
        public UsuarioEnMemoria(String username, String passwordHash, String rol) {
            this.username = username;
            this.passwordHash = passwordHash;
            this.rol = rol;
        }
    }
}