/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.profeco.quejas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    
    // CORREGIR ESTA LÍNEA: AGREGAR /api
    @Value("${auth.service.url:http://localhost:8081/api}")
    private String authServiceUrl;
    
    private final RestTemplate restTemplate;
    
    public AuthService() {
        this.restTemplate = new RestTemplate();
    }
    
    public boolean validarToken(String token) {
        try {
            System.out.println("=".repeat(50));
            System.out.println("🔐 [Quejas-AuthService] Validando token...");
            System.out.println("📡 authServiceUrl config: " + authServiceUrl);
            
            String url = authServiceUrl + "/auth/validar";
            System.out.println("🌐 URL completa: " + url);
            System.out.println("🔑 Token (truncado): " + 
                (token != null && token.length() > 20 ? token.substring(0, 20) + "..." : token));
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            System.out.println("📤 Enviando POST a Auth Service...");
            ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                Map.class
            );
            
            System.out.println("📥 Response Status: " + response.getStatusCode());
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                boolean esValido = Boolean.TRUE.equals(response.getBody().get("valido"));
                System.out.println("✅ Token válido: " + esValido);
                System.out.println("📊 Response Body: " + response.getBody());
                return esValido;
            }
            
        } catch (Exception e) {
            System.err.println("💥 ERROR validando token: " + e.getMessage());
            if (e.getMessage().contains("404")) {
                System.err.println("⚠️  Posible problema de URL. Verifica que exista: " + authServiceUrl + "/auth/validar");
                System.err.println("⚠️  Auth Service debe estar en: http://localhost:8081/api/auth/validar");
            }
        } finally {
            System.out.println("=".repeat(50));
        }
        
        return false;
    }
    
    public String obtenerRolDesdeToken(String token) {
        Map<String, Object> claims = obtenerClaimsDesdeToken(token);
        if (claims != null && claims.containsKey("rol")) {
            return (String) claims.get("rol");
        }
        return null;
    }

    public Map<String, Object> obtenerClaimsDesdeToken(String token) {
        try {
            System.out.println("=".repeat(50));
            System.out.println("🔍 [Quejas-AuthService] Obteniendo claims desde token...");
            System.out.println("📡 authServiceUrl config: " + authServiceUrl);
            
            String url = authServiceUrl + "/auth/validar";
            System.out.println("🌐 URL completa: " + url);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            System.out.println("📤 Enviando POST a Auth Service...");
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
                    System.out.println("✅ Claims obtenidos: " + body);
                    return body;
                } else {
                    System.out.println("❌ Token marcado como inválido en response");
                }
            } else {
                System.out.println("❌ Response no es OK o body es null");
            }
            
        } catch (Exception e) {
            System.err.println("💥 ERROR obteniendo claims: " + e.getClass().getSimpleName());
            System.err.println("💥 Mensaje: " + e.getMessage());
            
            if (e instanceof org.springframework.web.client.HttpClientErrorException) {
                org.springframework.web.client.HttpClientErrorException ex = 
                    (org.springframework.web.client.HttpClientErrorException) e;
                System.err.println("💥 Status Code: " + ex.getStatusCode());
                System.err.println("💥 Response Body: " + ex.getResponseBodyAsString());
            }
        } finally {
            System.out.println("=".repeat(50));
        }
        
        System.out.println("❌ Retornando null - no se pudieron obtener claims");
        return null;
    }
}