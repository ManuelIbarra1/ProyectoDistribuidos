/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.profeco.web.service;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

@ApplicationScoped
public class RestClient {
    
    private static final String API_GATEWAY = "http://localhost:8080/api";
    private Client client;
    private final ObjectMapper mapper = new ObjectMapper();
    
    @PostConstruct
    public void init() {
        this.client = ClientBuilder.newClient();
    }
    
    @PreDestroy
    public void cleanup() {
        if (client != null) {
            client.close();
        }
    }
    
    public String post(String endpoint, Map<String, Object> data) {
        return post(endpoint, data, null);
    }
    
    public String post(String endpoint, Map<String, Object> data, String token) {
        try {
            WebTarget target = client.target(API_GATEWAY).path(endpoint);
            Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
            
            if (token != null && !token.isEmpty()) {
                builder.header("Authorization", "Bearer " + token);
            }
            
            String json = mapper.writeValueAsString(data);
            Response response = builder.post(Entity.entity(json, MediaType.APPLICATION_JSON));
            
            if (response.getStatus() == 200) {
                return response.readEntity(String.class);
            } else {
                return "{\"error\":\"HTTP " + response.getStatus() + "\"}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }
    
    public String get(String endpoint, String token) {
        try {
            WebTarget target = client.target(API_GATEWAY).path(endpoint);
            Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
            
            if (token != null && !token.isEmpty()) {
                builder.header("Authorization", "Bearer " + token);
            }
            
            Response response = builder.get();
            
            if (response.getStatus() == 200) {
                return response.readEntity(String.class);
            } else {
                return "{\"error\":\"HTTP " + response.getStatus() + "\"}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }
}