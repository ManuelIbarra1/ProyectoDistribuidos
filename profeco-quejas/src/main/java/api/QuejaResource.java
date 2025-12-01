/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package api;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import entity.Queja;
import messaging.NotificacionService;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 *
 * @author Carlo
 */
@Path("/quejas")
public class QuejaResource {
   
   private static final Map<String, Queja> quejasDB = new ConcurrentHashMap<>();
    
    @Inject
    private NotificacionService notificacionService;
    
    // ✅ MÉTODO GET PARA OBTENER TODAS LAS QUEJAS
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerTodasLasQuejas() {
        try {
            System.out.println("📋 Solicitando listado de quejas");
            
            List<Queja> quejas = new ArrayList<>(quejasDB.values());
            
            // Ordenar por fecha (más recientes primero)
            quejas.sort((q1, q2) -> {
                if (q1.getFecha() == null || q2.getFecha() == null) return 0;
                return q2.getFecha().compareTo(q1.getFecha());
            });
            
            System.out.println("✅ Enviando " + quejas.size() + " quejas");
            
            // Crear JSON manualmente
            StringBuilder jsonResponse = new StringBuilder();
            jsonResponse.append("{\"total\": ").append(quejas.size()).append(", \"quejas\": [");
            
            for (int i = 0; i < quejas.size(); i++) {
                Queja q = quejas.get(i);
                jsonResponse.append("{")
                    .append("\"id\": \"").append(q.getId() != null ? q.getId() : "").append("\",")
                    .append("\"usuario\": \"").append(q.getUsuario() != null ? q.getUsuario() : "").append("\",")
                    .append("\"titulo\": \"").append(q.getTitulo() != null ? q.getTitulo() : "").append("\",")
                    .append("\"descripcion\": \"").append(q.getDescripcion() != null ? q.getDescripcion() : "").append("\",")
                    .append("\"comercio\": \"").append(q.getComercio() != null ? q.getComercio() : "").append("\",")
                    .append("\"fecha\": \"").append(q.getFecha() != null ? q.getFecha() : "").append("\",")
                    .append("\"estado\": \"").append(q.getEstado() != null ? q.getEstado() : "").append("\"")
                    .append("}");
                
                if (i < quejas.size() - 1) {
                    jsonResponse.append(",");
                }
            }
            
            jsonResponse.append("]}");
            
            return Response.ok(jsonResponse.toString()).build();
            
        } catch (Exception e) {
            System.err.println("💥 Error obteniendo quejas: " + e.getMessage());
            return Response.status(500).entity("{\"error\": \"Error obteniendo quejas\"}").build();
        }
    }
    
    // ✅ MÉTODO POST PARA CREAR QUEJAS (ya lo tienes)
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response crearQueja(Queja queja) {
        try {
            System.out.println("📨 Queja recibida: " + queja.getTitulo());
            
            // Validaciones
            if (queja.getTitulo() == null || queja.getTitulo().trim().isEmpty()) {
                return Response.status(400).entity("{\"error\": \"El título es requerido\"}").build();
            }
            
            if (queja.getUsuario() == null || queja.getUsuario().trim().isEmpty()) {
                return Response.status(400).entity("{\"error\": \"El usuario es requerido\"}").build();
            }
            
            // Generar ID único
            String quejaId = "Q-" + System.currentTimeMillis() + "-" + new Random().nextInt(1000);
            queja.setId(quejaId);
            
            // Guardar en memoria
            quejasDB.put(quejaId, queja);
            System.out.println("✅ Queja guardada - ID: " + quejaId);
            
            // 📤 ENVIAR A RABBITMQ
            notificacionService.enviarNotificacionQueja(queja.getUsuario(), quejaId, queja.getTitulo());
            
            return Response.status(201).entity("{" +
                "\"id\": \"" + quejaId + "\"," +
                "\"mensaje\": \"Queja registrada exitosamente\"," +
                "\"estado\": \"RECIBIDA\"," +
                "\"notificacion\": \"enviada a RabbitMQ\"," +
                "\"fecha\": \"" + queja.getFecha() + "\"" +
            "}").build();
            
        } catch (Exception e) {
            System.err.println("💥 Error creando queja: " + e.getMessage());
            return Response.status(500).entity("{\"error\": \"Error interno del servidor\"}").build();
        }
    }
    
    // Los otros métodos GET específicos...
}
