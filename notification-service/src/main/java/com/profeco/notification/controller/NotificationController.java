/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.profeco.notification.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "OK", "service", "notification-service");
    }
    
    @GetMapping("/test")
    public Map<String, String> test() {
        return Map.of("message", "Notification service is running!");
    }
}
