/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.profeco.web.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "QuejasServlet", urlPatterns = {
    "/quejas/*", "/dashboard", "/mis-quejas", "/crear-queja"
})
public class QuejasServlet extends HttpServlet {
    
    private static final String API_GATEWAY = "http://localhost:8080/api";
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("token") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String token = (String) session.getAttribute("token");
        String usuario = (String) session.getAttribute("usuario");
        String rol = (String) session.getAttribute("rol");
        String path = request.getServletPath();
        
        try {
            if ("/dashboard".equals(path) || "/mis-quejas".equals(path)) {
                // Obtener quejas del usuario
                String result = callApi("GET", "/quejas/usuario/" + usuario, null, token);
                Map<String, Object> responseMap = mapper.readValue(result, Map.class);
                
                request.setAttribute("quejas", responseMap.get("quejas"));
                request.setAttribute("total", responseMap.get("total"));
                request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp")
                       .forward(request, response);
                
            } else if ("/crear-queja".equals(path)) {
                request.getRequestDispatcher("/WEB-INF/views/crear-queja.jsp")
                       .forward(request, response);
                        
            } else if (path.startsWith("/quejas/") && "profeco".equals(rol)) {
                // Ruta para PROFECO ver todas las quejas
                String result = callApi("GET", "/quejas", null, token);
                Map<String, Object> responseMap = mapper.readValue(result, Map.class);
                
                request.setAttribute("quejas", responseMap.get("quejas"));
                request.setAttribute("total", responseMap.get("total"));
                request.getRequestDispatcher("/WEB-INF/views/admin-quejas.jsp")
                       .forward(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al cargar las quejas: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp")
                   .forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("token") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String token = (String) session.getAttribute("token");
        String usuario = (String) session.getAttribute("usuario");
        
        if ("/crear-queja".equals(request.getServletPath())) {
            crearQueja(request, response, token, usuario);
        }
    }
    
    private void crearQueja(HttpServletRequest request, HttpServletResponse response, 
                           String token, String usuario) throws ServletException, IOException {
        
        Map<String, Object> quejaData = new HashMap<>();
        quejaData.put("titulo", request.getParameter("titulo"));
        quejaData.put("descripcion", request.getParameter("descripcion"));
        quejaData.put("comercio", request.getParameter("comercio"));
        quejaData.put("usuario", usuario);
        
        String result = callApi("POST", "/quejas", quejaData, token);
        
        try {
            Map<String, Object> responseMap = mapper.readValue(result, Map.class);
            
            if (responseMap.containsKey("mensaje")) {
                request.setAttribute("success", "Queja registrada exitosamente");
                response.sendRedirect(request.getContextPath() + "/dashboard");
            } else {
                request.setAttribute("error", responseMap.get("error"));
                request.getRequestDispatcher("/WEB-INF/views/crear-queja.jsp")
                       .forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al crear la queja: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/crear-queja.jsp")
                   .forward(request, response);
        }
    }
    
    // Método para llamar APIs REST
    private String callApi(String method, String endpoint, Map<String, Object> data, String token) {
        try {
            String url = API_GATEWAY + endpoint;
            
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
            
            if (token != null && !token.isEmpty()) {
                requestBuilder.header("Authorization", "Bearer " + token);
            }
            
            HttpRequest request;
            if ("POST".equalsIgnoreCase(method) && data != null) {
                String json = mapper.writeValueAsString(data);
                request = requestBuilder.POST(HttpRequest.BodyPublishers.ofString(json)).build();
            } else {
                request = requestBuilder.GET().build();
            }
            
            HttpResponse<String> httpResponse = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            return httpResponse.body();
            
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }
}