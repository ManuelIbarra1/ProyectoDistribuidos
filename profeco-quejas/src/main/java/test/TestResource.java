/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/test")
public class TestResource {
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "✅ Jersey está funcionando correctamente!";
    }
    
    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    public String testJson() {
        return "{\"mensaje\": \"JSON funcionando\", \"status\": \"ok\"}";
    }
}
