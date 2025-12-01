/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.profeco.auth.model;
/**
 *
 * @author Carlo
 */
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    @NotBlank(message = "El usuario es obligatorio")
    private String username;
    
    @Column(nullable = false)
    @NotBlank(message = "La contraseña es obligatoria")
    private String passwordHash;
    
    @Column(nullable = false)
    private String rol;
    
    private LocalDateTime fechaCreacion;
    
    // Constructores
    public Usuario() {
        this.fechaCreacion = LocalDateTime.now();
    }
    
    public Usuario(String username, String passwordHash, String rol) {
        this();
        this.username = username;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
