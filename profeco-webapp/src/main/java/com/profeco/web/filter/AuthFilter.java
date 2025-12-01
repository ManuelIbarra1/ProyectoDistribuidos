/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.profeco.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter(filterName = "AuthFilter", urlPatterns = {"/dashboard/*", "/quejas/*", "/admin/*"})
public class AuthFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                         FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        String loginURI = httpRequest.getContextPath() + "/login";
        boolean loggedIn = session != null && session.getAttribute("token") != null;
        boolean loginRequest = httpRequest.getRequestURI().equals(loginURI);
        
        if (loggedIn || loginRequest) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(loginURI);
        }
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}
    
    @Override
    public void destroy() {}
}
