package com.example.fronted_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.fronted_service.dto.CarritoDTO;
import com.example.fronted_service.dto.ItemCarritoDTO;

import java.util.Map;

@FeignClient(name = "carrito-service", url = "${carrito.service.url:http://localhost:8083}")
public interface CarritoFeign{
    
    // Obtener carrito por cliente
    @GetMapping("/api/carrito/cliente/{clienteId}")
    ResponseEntity<CarritoDTO> obtenerCarrito(@PathVariable("clienteId") Long clienteId);
    
    // Agregar item al carrito
    @PostMapping("/api/carrito/cliente/{clienteId}/items")
    ResponseEntity<CarritoDTO> agregarItem(
            @PathVariable("clienteId") Long clienteId,
            @RequestBody ItemCarritoDTO itemDTO);
    
    // Actualizar cantidad
    @PutMapping("/api/carrito/cliente/{clienteId}/items/{productoId}")
    ResponseEntity<CarritoDTO> actualizarCantidad(
            @PathVariable("clienteId") Long clienteId,
            @PathVariable("productoId") Long productoId,
            @RequestParam("cantidad") Integer cantidad);
    
    // Eliminar item
    @DeleteMapping("/api/carrito/cliente/{clienteId}/items/{productoId}")
    ResponseEntity<CarritoDTO> eliminarItem(
            @PathVariable("clienteId") Long clienteId,
            @PathVariable("productoId") Long productoId);
    
    // Vaciar carrito
    @DeleteMapping("/api/carrito/cliente/{clienteId}")
    ResponseEntity<Void> vaciarCarrito(@PathVariable("clienteId") Long clienteId);
    
    // Obtener resumen
    @GetMapping("/api/carrito/cliente/{clienteId}/resumen")
    ResponseEntity<Map<String, Object>> obtenerResumen(@PathVariable("clienteId") Long clienteId);
    
    // Verificar si está vacío
    @GetMapping("/api/carrito/cliente/{clienteId}/vacio")
    ResponseEntity<Map<String, Boolean>> estaVacio(@PathVariable("clienteId") Long clienteId);
}