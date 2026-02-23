package com.example.fronted_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.example.fronted_service.dto.PageDTO;
import com.example.fronted_service.dto.ProductoDTO;

@FeignClient(name = "product-service", url = "http://localhost:8081")
public interface ProductoFeign {

    // ============ MÉTODOS PARA ADMINISTRACIÓN ============

    @GetMapping("/api/productos")
    PageDTO<ProductoDTO> listarPaginado(
            @RequestParam("page") int page,
            @RequestParam("size") int size);

    @GetMapping("/api/productos/buscar")
    PageDTO<ProductoDTO> buscar(
            @RequestParam("q") String q,
            @RequestParam("page") int page,
            @RequestParam("size") int size);

    @GetMapping("/api/productos/{id}")
    ProductoDTO buscarPorId(@PathVariable("id") Long id);

    @PostMapping("/api/productos")
    ProductoDTO crear(
            @RequestBody ProductoDTO producto,
            @RequestParam("categoriaId") Long categoriaId);

    @PutMapping("/api/productos/{id}")
    ProductoDTO actualizar(
            @PathVariable("id") Long id,
            @RequestBody ProductoDTO producto,
            @RequestParam("categoriaId") Long categoriaId);

    @PatchMapping("/api/productos/{id}/estado")
    void habilitar(
            @PathVariable("id") Long id,
            @RequestParam("habilitado") boolean habilitado);

    @DeleteMapping("/api/productos/{id}")
    void eliminar(@PathVariable("id") Long id);

    // ============ MÉTODOS PARA CLIENTES (NUEVOS) ============

    /**
     * Listar productos habilitados (para el menú del cliente)
     */
    @GetMapping("/api/productos/habilitados")
    PageDTO<ProductoDTO> listarHabilitados(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size);

    /**
     * Listar productos por categoría (para filtrar en el menú)
     */
    @GetMapping("/api/productos/categoria/{categoriaId}")
    PageDTO<ProductoDTO> listarPorCategoria(
            @PathVariable("categoriaId") Long categoriaId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size);

    /**
     * Listar productos destacados
     */
    @GetMapping("/api/productos/destacados")
    PageDTO<ProductoDTO> listarDestacados(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size);

    /**
     * Buscar productos habilitados (para clientes)
     */
    @GetMapping("/api/productos/habilitados/buscar")
    PageDTO<ProductoDTO> buscarHabilitados(
            @RequestParam("q") String q,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size);
}