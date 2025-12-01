/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/hola")
public class HolaResource {
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hola() {
        return "¡HOLA MUNDO! JAX-RS FUNCIONA";
    }
}
