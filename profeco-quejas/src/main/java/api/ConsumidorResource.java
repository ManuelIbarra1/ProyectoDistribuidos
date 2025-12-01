/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package api;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/consumidor")
public class ConsumidorResource {
    
    @GET
    @Path("/mis-quejas")
    @Produces(MediaType.TEXT_PLAIN)
    public String misQuejas() {
        return "📝 Mis quejas registradas - Área de consumidores";
    }
    
    @GET
    @Path("/perfil")
    @Produces(MediaType.TEXT_PLAIN)
    public String perfil() {
        return "👤 Mi perfil de consumidor";
    }
}
