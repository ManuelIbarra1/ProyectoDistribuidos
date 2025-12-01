/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author Carlo
 */
public class Notificacion {
   public String destinatario;
    public String asunto;
    public String mensaje;
    public String quejaId;
    public String tipo;
    public String timestamp;
    
    public Notificacion(String destinatario, String asunto, String mensaje, 
                       String quejaId, String tipo, String timestamp) {
        this.destinatario = destinatario;
        this.asunto = asunto;
        this.mensaje = mensaje;
        this.quejaId = quejaId;
        this.tipo = tipo;
        this.timestamp = timestamp;
    }
    
    // Constructor vacío para Jackson
    public Notificacion() {}
    
    @Override
    public String toString() {
        return "Notificacion{" +
                "destinatario='" + destinatario + '\'' +
                ", asunto='" + asunto + '\'' +
                ", quejaId='" + quejaId + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}
