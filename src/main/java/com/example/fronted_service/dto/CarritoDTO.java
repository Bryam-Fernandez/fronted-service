// CarritoDTO.java - Usando BigDecimal
package com.example.fronted_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class CarritoDTO {
    
    private Long id;
    private Long clienteId;
    private List<ItemCarritoDTO> items;
    private BigDecimal total; // Cambiado a BigDecimal
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private Integer cantidadItems;
    
    // Constructor vacío
    public CarritoDTO() {}
    
    // Constructor con parámetros principales
    public CarritoDTO(Long id, Long clienteId, List<ItemCarritoDTO> items) {
        this.id = id;
        this.clienteId = clienteId;
        this.items = items;
        calcularTotales();
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getClienteId() {
        return clienteId;
    }
    
    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }
    
    public List<ItemCarritoDTO> getItems() {
        return items;
    }
    
    public void setItems(List<ItemCarritoDTO> items) {
        this.items = items;
        calcularTotales();
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
    
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
    
    public Integer getCantidadItems() {
        return cantidadItems;
    }
    
    public void setCantidadItems(Integer cantidadItems) {
        this.cantidadItems = cantidadItems;
    }
    
    // Método para calcular totales automáticamente
    private void calcularTotales() {
        if (items != null && !items.isEmpty()) {
            this.total = items.stream()
                .map(ItemCarritoDTO::getSubtotal)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            this.cantidadItems = items.size();
        } else {
            this.total = BigDecimal.ZERO;
            this.cantidadItems = 0;
        }
    }
    
    // Método para verificar si el carrito está vacío
    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }
    
    // Método para obtener resumen del carrito
    public String getResumen() {
        return String.format("Carrito #%d - Cliente: %d - Items: %d - Total: $%s", 
            id, clienteId, cantidadItems, total != null ? total.toString() : "0.00");
    }
    
    @Override
    public String toString() {
        return "CarritoDTO{" +
                "id=" + id +
                ", clienteId=" + clienteId +
                ", cantidadItems=" + cantidadItems +
                ", total=" + total +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}