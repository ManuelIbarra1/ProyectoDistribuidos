/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.profeco.auth.security;
//import jakarta.annotation.Priority;
//import jakarta.ws.rs.Priorities;
//import jakarta.ws.rs.container.ContainerRequestContext;
//import jakarta.ws.rs.container.ContainerRequestFilter;
//import jakarta.ws.rs.core.Response;
//import jakarta.ws.rs.ext.Provider;
//import java.io.IOException;
///**
// *
// * @author Carlo
// */
//@Provider
//@Priority(Priorities.AUTHORIZATION)
//public class RoleFilter implements ContainerRequestFilter {
//
//    @Override
//    public void filter(ContainerRequestContext requestContext) throws IOException {
//        String path = requestContext.getUriInfo().getPath();
//        String method = requestContext.getMethod();
//        
//        System.out.println("🔐 RoleFilter - Path: " + path + ", Method: " + method);
//        
//        // 1. Endpoints públicos - permitir sin token
//        if (isPublicEndpoint(path, method)) {
//            System.out.println("✅ Endpoint público - acceso permitido");
//            return;
//        }
//        
//        // 2. Extraer token
//        String authHeader = requestContext.getHeaderString("Authorization");
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            System.out.println("❌ No hay token en la request");
//            requestContext.abortWith(
//                Response.status(401).entity("{\"error\": \"Token de autorización requerido\"}").build()
//            );
//            return;
//        }
//        
//        String token = authHeader.substring(7);
//        
//        try {
//            // 3. Validar token
//            if (!JwtUtil.validarToken(token)) {
//                System.out.println("❌ Token inválido o expirado");
//                requestContext.abortWith(
//                    Response.status(401).entity("{\"error\": \"Token inválido o expirado\"}").build()
//                );
//                return;
//            }
//            
//            // 4. Extraer información del token
//            String rol = JwtUtil.obtenerRol(token);
//            String usuario = JwtUtil.obtenerUsuario(token);
//            
//            System.out.println("🔍 Usuario: " + usuario + ", Rol: " + rol + ", Path: " + path);
//            
//            // 5. Verificar permisos según el rol y el endpoint
//            if (!hasPermission(rol, path, method)) {
//                System.out.println("❌ Acceso denegado - Rol '" + rol + "' no tiene permisos para: " + path);
//                requestContext.abortWith(
//                    Response.status(403).entity("{\"error\": \"Acceso denegado. No tienes permisos para este recurso\"}").build()
//                );
//                return;
//            }
//            
//            System.out.println("✅ Acceso autorizado para rol: " + rol);
//            
//        } catch (Exception e) {
//            System.out.println("💥 Error en validación: " + e.getMessage());
//            requestContext.abortWith(
//                Response.status(401).entity("{\"error\": \"Error en autenticación\"}").build()
//            );
//        }
//    }
//    
//    private boolean isPublicEndpoint(String path, String method) {
//        // Endpoints que no requieren autenticación
//        return (path.equals("auth/login") && method.equals("POST")) ||
//               (path.equals("auth/registro") && method.equals("POST")) ||
//               (path.equals("hola") && method.equals("GET")) ||
//               (path.equals("test") && method.equals("GET")) ||
//               (path.equals("test/json") && method.equals("GET"));
//        // ❌ QUITAMOS quejas POST - ahora requiere autenticación
//    }
//    
//    private boolean hasPermission(String rol, String path, String method) {
//        // ✅ SOLO CONSUMIDORES pueden crear quejas
//        if (path.equals("quejas") && method.equals("POST")) {
//            return "consumidor".equals(rol); // Solo rol "consumidor"
//        }
//        
//        // 🔓 Cualquier usuario autenticado puede ver quejas específicas
//        if (path.equals("quejas") && method.equals("GET")) {
//            return true; // Cualquier rol autenticado
//        }
//        
//        if (path.startsWith("quejas/usuario/") && method.equals("GET")) {
//            return true; // Cualquier rol autenticado
//        }
//        
//        if (path.matches("quejas/Q-.*") && method.equals("GET")) {
//            return true; // Cualquier rol autenticado puede ver queja específica
//        }
//        
//        // 🔒 Endpoints de administración
//        if ("profeco".equals(rol)) {
//            return true; // Los administradores pueden acceder a todo
//        }
//        
//        // 🔒 Endpoints de consumidores
//        if ("consumidor".equals(rol)) {
//            return path.startsWith("consumidor/");
//        }
//        
//        // Por defecto, denegar acceso
//        return false;
//    }
//}