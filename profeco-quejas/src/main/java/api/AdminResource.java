/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package api;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/admin")
public class AdminResource {
    
    @GET
    @Path("/dashboard")
    @Produces(MediaType.TEXT_PLAIN)
    public String dashboard() {
        return "🔧 Panel de Administración PROFECO - Solo personal autorizado";
    }
    
    @GET
    @Path("/estadisticas")
    @Produces(MediaType.TEXT_PLAIN)
    public String estadisticas() {
        return "📊 Estadísticas del sistema - Solo PROFECO";
    }
}
