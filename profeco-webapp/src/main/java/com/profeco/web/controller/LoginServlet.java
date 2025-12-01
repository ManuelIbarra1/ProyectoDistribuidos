/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.profeco.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login", "/logout", "/register"})
public class LoginServlet extends HttpServlet {
    
    private static final String API_GATEWAY = "http://localhost:8080/api";
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    
    // ... resto de métodos doGet, doPost igual ...
    
    private String callApi(String method, String endpoint, Map<String, String> data, String token) {
        try {
            String url = API_GATEWAY + endpoint;
            String json = mapper.writeValueAsString(data);
            
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json");
            
            if (token != null && !token.isEmpty()) {
                requestBuilder.header("Authorization", "Bearer " + token);
            }
            
            HttpRequest request;
            if ("POST".equalsIgnoreCase(method)) {
                request = requestBuilder.POST(HttpRequest.BodyPublishers.ofString(json)).build();
            } else {
                request = requestBuilder.GET().build();
            }
            
            HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            return response.body();
            
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }
}