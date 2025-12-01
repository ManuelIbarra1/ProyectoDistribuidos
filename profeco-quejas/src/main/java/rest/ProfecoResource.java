/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rest;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
/**
 *
 * @author Carlo
 */
@Path("/profeco")
public class ProfecoResource {

  @GET
  @Path("/asignar/{id}")
  @RolesAllowed("profeco")
  @Produces(MediaType.APPLICATION_JSON)
  public String asignar(@PathParam("id") String id) {
    // solo usuarios con rol 'profeco' en token pueden acceder
    return "Asignado";
  }
}