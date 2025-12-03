package com.profeco.quejas.dto;

public class QuejaRequest {
    private String titulo;
    private String descripcion;
    private String comercio;
    
    // Constructores
    public QuejaRequest() {}
    
    public QuejaRequest(String titulo, String descripcion, String comercio) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.comercio = comercio;
    }
    
    // Getters y Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getComercio() { return comercio; }
    public void setComercio(String comercio) { this.comercio = comercio; }
    
    @Override
    public String toString() {
        return "QuejaRequest{" +
               "titulo='" + titulo + '\'' +
               ", descripcion='" + descripcion + '\'' +
               ", comercio='" + comercio + '\'' +
               '}';
    }
}
