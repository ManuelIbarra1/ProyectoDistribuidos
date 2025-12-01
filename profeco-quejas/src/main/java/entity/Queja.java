/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;
import java.time.LocalDateTime;
/**
 *
 * @author Carlo
 */
public class Queja {
      private String id;
    private String usuario;
    private String titulo;
    private String descripcion;
    private String comercio;
    private String fecha;
    private String estado;

    public Queja() {
        this.fecha = LocalDateTime.now().toString();
        this.estado = "RECIBIDA";
    }

    public Queja(String usuario, String titulo, String descripcion, String comercio) {
        this();
        this.usuario = usuario;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.comercio = comercio;
    }

    // Getters y Setters (asegúrate de tenerlos todos)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getComercio() { return comercio; }
    public void setComercio(String comercio) { this.comercio = comercio; }
    
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    @Override
    public String toString() {
        return "Queja{id='" + id + "', usuario='" + usuario + "', titulo='" + titulo + "'}";
    }
}