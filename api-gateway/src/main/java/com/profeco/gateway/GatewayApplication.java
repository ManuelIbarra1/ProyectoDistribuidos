package com.profeco.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(GatewayApplication.class)
            .properties("server.port=8085")
            .run(args);
        
        System.out.println("API Gateway corriendo en puerto 8085");
        System.out.println("CORS configurado para desarrollo");
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("auth-service", r -> r.path("/api/auth/**")
                .uri("http://localhost:8081"))
            .route("quejas-service", r -> r.path("/api/quejas/**")
                .uri("http://localhost:8082"))
            .route("notification-service", r -> r.path("/api/notifications/**")
                .uri("http://localhost:8083"))
            .build();
    }
    
    // ⭐⭐⭐ FILTRO CORS SUPER PERMISIVO (SOLO DESARROLLO) ⭐⭐⭐
    // En GatewayApplication.java - YA LO TIENES BIEN
@Bean
public WebFilter corsFilter() {
    return (ServerWebExchange ctx, WebFilterChain chain) -> {
        ServerHttpRequest request = ctx.getRequest();
        ServerHttpResponse response = ctx.getResponse();
        
        // Permitir GlassFish (8080)
        response.getHeaders().add("Access-Control-Allow-Origin", "http://localhost:8080");
        response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.getHeaders().add("Access-Control-Allow-Headers", "*");
        response.getHeaders().add("Access-Control-Allow-Credentials", "true");
        response.getHeaders().add("Access-Control-Max-Age", "3600");
        
        if (request.getMethod() == HttpMethod.OPTIONS) {
            response.setStatusCode(HttpStatus.OK);
            return Mono.empty();
        }
        
        return chain.filter(ctx);
    };
}
}
