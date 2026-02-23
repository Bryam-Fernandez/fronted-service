// ItemCarritoDTO.java
package com.example.fronted_service.dto;

import java.math.BigDecimal;

public class ItemCarritoDTO {
    
    private Long id;
    private Long productoId;
    private String productoNombre;
    private BigDecimal productoPrecio;
    private int cantidad;
    private String tamaño;
    private String toppings;
    private String imagenUrl;
    private BigDecimal subtotal; // BigDecimal
    
    public ItemCarritoDTO() {}
    
    public ItemCarritoDTO(Long productoId, String productoNombre, BigDecimal productoPrecio, 
                          int cantidad, String tamaño, String toppings, String imagenUrl) {
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.productoPrecio = productoPrecio;
        this.cantidad = cantidad;
        this.tamaño = tamaño;
        this.toppings = toppings;
        this.imagenUrl = imagenUrl;
        calcularSubtotal();
    }
    
    public ItemCarritoDTO(ProductoDTO producto, int cantidad2, String tamaño2, String toppingsString,
			String imagenUrl2) {
	}

	// Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    
    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
    
    public BigDecimal getProductoPrecio() { return productoPrecio; }
    public void setProductoPrecio(BigDecimal productoPrecio) { 
        this.productoPrecio = productoPrecio;
        calcularSubtotal();
    }
    
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { 
        this.cantidad = cantidad;
        calcularSubtotal();
    }
    
    public String getTamaño() { return tamaño; }
    public void setTamaño(String tamaño) { this.tamaño = tamaño; }
    
    public String getToppings() { return toppings; }
    public void setToppings(String toppings) { this.toppings = toppings; }
    
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    private void calcularSubtotal() {
        if (productoPrecio != null) {
            this.subtotal = productoPrecio.multiply(BigDecimal.valueOf(cantidad));
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
    }
}