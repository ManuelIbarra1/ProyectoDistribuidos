package com.profeco.web.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@WebServlet("/api/*")
public class ApiServlet extends HttpServlet {
    
    private static final String GATEWAY_URL = "http://localhost:8085";
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            response.sendError(400, "Path requerido");
            return;
        }
        
        String targetUrl = GATEWAY_URL + pathInfo;
        System.out.println("Proxy POST to: " + targetUrl);
        
        // Leer body de la solicitud
        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        
        // Enviar al Gateway
        HttpURLConnection conn = (HttpURLConnection) new URL(targetUrl).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        
        // Enviar body
        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBody.toString().getBytes("UTF-8"));
        }
        
        // Leer respuesta
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                response.getWriter().write(line);
            }
        }
    }
}