/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.profeco.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    
    @GetMapping("/auth")
    public Map<String, String> authFallback() {
        return Map.of(
            "status", "SERVICE_UNAVAILABLE",
            "message", "Auth service is temporarily unavailable",
            "service", "auth-service"
        );
    }
    
    @GetMapping("/quejas")
    public Map<String, String> quejasFallback() {
        return Map.of(
            "status", "SERVICE_UNAVAILABLE", 
            "message", "Quejas service is temporarily unavailable",
            "service", "quejas-service"
        );
    }
    
    @GetMapping("/notifications")
    public Map<String, String> notificationsFallback() {
        return Map.of(
            "status", "SERVICE_UNAVAILABLE",
            "message", "Notification service is temporarily unavailable", 
            "service", "notification-service"
        );
    }
}
