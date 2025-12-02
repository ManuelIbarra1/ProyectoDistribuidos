/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.profeco.notification.model;
import java.time.LocalDateTime;
/**
 *
 * @author Carlo
 */
public class Notificacion {
    private String destinatario;
    private String asunto;
    private String mensaje;
    private String quejaId;
    private String tipo;
    private String timestamp;
    private boolean enviada;

    // --- Campos adicionales ---
    private String usuario;
    private String titulo;
    private String comercio;
    private String fecha;
    
    // Constructores, getters y setters
    public Notificacion() {}
    
    public Notificacion(String destinatario, String asunto, String mensaje, 
                       String quejaId, String tipo) {
        this.destinatario = destinatario;
        this.asunto = asunto;
        this.mensaje = mensaje;
        this.quejaId = quejaId;
        this.tipo = tipo;
        this.timestamp = LocalDateTime.now().toString();
        this.enviada = false;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getQuejaId() {
        return quejaId;
    }

    public void setQuejaId(String quejaId) {
        this.quejaId = quejaId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isEnviada() {
        return enviada;
    }

    public void setEnviada(boolean enviada) {
        this.enviada = enviada;
    }

    // --- Getters y Setters para campos adicionales ---

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getComercio() {
        return comercio;
    }

    public void setComercio(String comercio) {
        this.comercio = comercio;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
