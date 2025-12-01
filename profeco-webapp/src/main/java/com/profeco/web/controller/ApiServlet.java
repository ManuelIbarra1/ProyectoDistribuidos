package com.profeco.web.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

@WebServlet("/api/*")
public class ApiServlet extends HttpServlet {
    
    private static final String GATEWAY_URL = "http://localhost:8085";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        proxyRequest(req, resp, "GET");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String methodOverride = req.getParameter("_method");
        if (methodOverride != null && methodOverride.equalsIgnoreCase("DELETE")) {
            proxyRequest(req, resp, "DELETE");
        } else if (methodOverride != null && methodOverride.equalsIgnoreCase("PUT")) {
            proxyRequest(req, resp, "PUT");
        } else {
            proxyRequest(req, resp, "POST");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        proxyRequest(req, resp, "PUT");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        proxyRequest(req, resp, "DELETE");
    }

    private void proxyRequest(HttpServletRequest request, HttpServletResponse response, String method) 
            throws IOException {
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Path information is missing");
            return;
        }
        
        // Correctly construct the target URL for the gateway
        String targetUrl = GATEWAY_URL + "/api" + pathInfo;
        System.out.println("Proxying " + method + " to: " + targetUrl);
        
        HttpURLConnection conn = (HttpURLConnection) new URL(targetUrl).openConnection();
        conn.setRequestMethod(method);

        // Copy headers from original request to the proxy request
        // Especially important for Authorization (JWT)
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            if (headerName.equalsIgnoreCase("Authorization")) {
                conn.setRequestProperty(headerName, request.getHeader(headerName));
            }
        });
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");

        // For methods that can have a body, copy the request body
        if (method.equals("POST") || method.equals("PUT")) {
            conn.setDoOutput(true);
            String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes("UTF-8"));
            }
        }
        
        // Execute the request and handle the response
        int statusCode = conn.getResponseCode();
        response.setStatus(statusCode);
        response.setContentType(conn.getContentType());
        response.setCharacterEncoding("UTF-8");

        InputStream inputStream = (statusCode >= 200 && statusCode < 300) 
            ? conn.getInputStream() 
            : conn.getErrorStream();

        if (inputStream != null) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                PrintWriter out = response.getWriter();
                while ((line = in.readLine()) != null) {
                    out.write(line);
                }
                out.flush();
            }
        }
    }
}