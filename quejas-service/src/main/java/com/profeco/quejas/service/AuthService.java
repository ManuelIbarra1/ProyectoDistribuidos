package com.profeco.quejas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class AuthService {
    
    // ✅ CORREGIDO: Solo base URL, sin /api/auth
    @Value("${auth.service.url:http://localhost:8085}")
    private String authServiceUrl;
    
    private final RestTemplate restTemplate;
    
    public AuthService() {
        this.restTemplate = new RestTemplate();
    }
    
    public boolean validarToken(String token) {
        System.out.println("=".repeat(50));
        System.out.println("🔐 [Quejas-AuthService] Validando token via Gateway...");
        System.out.println("📡 Gateway URL: " + authServiceUrl);
        
        try {
            // ✅ CORREGIDO: URL correcta
            String url = authServiceUrl + "/api/auth/validar";
            System.out.println("✅ URL CORRECTA: " + url);
            System.out.println("🔑 Token (truncado): " + 
                (token != null && token.length() > 20 ? token.substring(0, 20) + "..." : token));
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            System.out.println("📤 Enviando POST a Gateway...");
            ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            System.out.println("📥 Response Status: " + response.getStatusCode());
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                boolean esValido = Boolean.TRUE.equals(body.get("valido"));
                System.out.println("✅ Token válido: " + esValido);
                System.out.println("📊 Response Body: " + body);
                System.out.println("=".repeat(50));
                return esValido;
            }
            
        } catch (Exception e) {
            System.err.println("💥 ERROR validando token: " + e.getMessage());
            System.err.println("⚠️  URL intentada: " + authServiceUrl + "/api/auth/validar");
        }
        
        System.out.println("❌ Token inválido o error en validación");
        System.out.println("=".repeat(50));
        return false;
    }
    
    public String obtenerRolDesdeToken(String token) {
        System.out.println("=".repeat(50));
        System.out.println("🔍 [Quejas-AuthService] Obteniendo rol desde token...");
        
        try {
            // ✅ CORREGIDO
            String url = authServiceUrl + "/api/auth/validar";
            System.out.println("✅ URL CORRECTA: " + url);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            System.out.println("📤 Enviando POST a Gateway para obtener rol...");
            ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            System.out.println("📥 Response Status: " + response.getStatusCode());
            System.out.println("📥 Response Body: " + response.getBody());
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                
                Boolean valido = (Boolean) body.get("valido");
                System.out.println("✓ Token válido: " + valido);
                
                if (Boolean.TRUE.equals(valido)) {
                    String rol = (String) body.get("rol");
                    System.out.println("✅ Rol obtenido: '" + rol + "'");
                    System.out.println("=".repeat(50));
                    return rol;
                }
            }
            
        } catch (Exception e) {
            System.err.println("💥 ERROR obteniendo rol: " + e.getMessage());
        }
        
        System.out.println("❌ Retornando null - no se pudo obtener rol");
        System.out.println("=".repeat(50));
        return null;
    }
    
    public String obtenerUsuarioDesdeToken(String token) {
        System.out.println("=".repeat(50));
        System.out.println("👤 [Quejas-AuthService] Obteniendo usuario desde token...");
        
        try {
            // ✅ CORREGIDO
            String url = authServiceUrl + "/api/auth/validar";
            System.out.println("✅ URL CORRECTA: " + url);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            System.out.println("📤 Enviando POST a Gateway para obtener usuario...");
            ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            System.out.println("📥 Response Status: " + response.getStatusCode());
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                
                Boolean valido = (Boolean) body.get("valido");
                System.out.println("✓ Token válido: " + valido);
                
                if (Boolean.TRUE.equals(valido)) {
                    String usuario = (String) body.get("usuario");
                    System.out.println("✅ Usuario obtenido: '" + usuario + "'");
                    System.out.println("=".repeat(50));
                    return usuario;
                }
            }
            
        } catch (Exception e) {
            System.err.println("💥 ERROR obteniendo usuario: " + e.getMessage());
        }
        
        System.out.println("❌ No se pudo obtener usuario del token");
        System.out.println("=".repeat(50));
        return null;
    }
}