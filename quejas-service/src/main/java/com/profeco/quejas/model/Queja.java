/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.profeco.quejas.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "quejas")
public class Queja {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String quejaId; // ID único para exposición pública
    
    @Column(nullable = false)
    @NotBlank(message = "El usuario es obligatorio")
    private String usuario;
    
    @Column(nullable = false)
    @NotBlank(message = "El título es obligatorio")
    private String titulo;
    
    @Column(nullable = false, length = 1000)
    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;
    
    @Column(nullable = false)
    @NotBlank(message = "El comercio es obligatorio")
    private String comercio;
    
    private LocalDateTime fecha;
    
    @Column(nullable = false)
    private String estado;
    
    // Constructores
    public Queja() {
        this.fecha = LocalDateTime.now();
        this.estado = "RECIBIDA";
        this.quejaId = generarQuejaId();
    }
    
    public Queja(String usuario, String titulo, String descripcion, String comercio) {
        this();
        this.usuario = usuario;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.comercio = comercio;
    }
    
    private String generarQuejaId() {
        return "Q-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getQuejaId() { return quejaId; }
    public void setQuejaId(String quejaId) { this.quejaId = quejaId; }
    
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getComercio() { return comercio; }
    public void setComercio(String comercio) { this.comercio = comercio; }
    
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
