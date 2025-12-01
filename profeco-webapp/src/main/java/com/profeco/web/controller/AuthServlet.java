/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.profeco.web.controller;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "AuthServlet", urlPatterns = {"/auth/login", "/auth/logout"})
public class AuthServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        
        if ("/auth/login".equals(path)) {
            // El login real ahora lo maneja JavaScript
            // Este endpoint solo verifica sesión
            HttpSession session = request.getSession();
            if (session.getAttribute("token") != null) {
                response.setStatus(200);
                response.getWriter().write("{\"status\":\"authenticated\"}");
            } else {
                response.setStatus(401);
                response.getWriter().write("{\"error\":\"No autenticado\"}");
            }
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if ("/auth/logout".equals(request.getServletPath())) {
            request.getSession().invalidate();
            response.sendRedirect(request.getContextPath() + "/login.html");
        }
    }
}
