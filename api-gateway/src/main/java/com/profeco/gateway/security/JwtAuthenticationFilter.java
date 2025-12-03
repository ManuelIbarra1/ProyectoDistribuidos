/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.profeco.gateway.security;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import io.jsonwebtoken.Jwts;
/**
 *
 * @author Carlo
 */
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final String SECRET = "MI_CLAVE_SECRETA_MUY_SEGURA_PARA_PROFECO_2024";

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            // Skip auth for public endpoints
            if (isPublicEndpoint(request)) {
                return chain.filter(exchange);
            }
            
            // Get token from header
            String token = getTokenFromRequest(request);
            
            if (token == null) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            
            try {
                // Validate token
                Jwts.parserBuilder()
                    .setSigningKey(SECRET.getBytes())
                    .build()
                    .parseClaimsJws(token);
                
                // Add user info to headers for downstream services
                String usuario = Jwts.parserBuilder()
                    .setSigningKey(SECRET.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
                
                String rol = Jwts.parserBuilder()
                    .setSigningKey(SECRET.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("rol", String.class);
                
                ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", usuario)
                    .header("X-User-Role", rol)
                    .build();
                
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
                
            } catch (Exception e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        };
    }
    
    private boolean isPublicEndpoint(ServerHttpRequest request) {
        String path = request.getPath().toString();
        String method = request.getMethod().toString();
        
        return (path.equals("/api/auth/login") && method.equals("POST")) ||
               (path.equals("/api/auth/registro") && method.equals("POST")) ||
               (path.startsWith("/api/notifications/health")) ||
               (path.startsWith("/fallback/"));
    }
    
    private String getTokenFromRequest(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public static class Config {
        // Configuration properties if needed
    }
}
