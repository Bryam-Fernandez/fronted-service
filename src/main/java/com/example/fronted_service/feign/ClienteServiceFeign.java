// fronted-service/src/main/java/com/example/fronted_service/feign/ClienteServiceFeign.java
package com.example.fronted_service.feign;

import com.example.fronted_service.dto.ClienteDTO;
import com.example.fronted_service.dto.PageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "cliente-service", url = "${cliente.service.url:http://localhost:8082}")
public interface ClienteServiceFeign {

    // ============ CRUD BÁSICO ============

    @GetMapping("/api/clientes")
    ResponseEntity<PageDTO<ClienteDTO>> listar(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @RequestParam(value = "direction", defaultValue = "asc") String direction);

    @GetMapping("/api/clientes/buscar")
    ResponseEntity<PageDTO<ClienteDTO>> buscar(
            @RequestParam("q") String q,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size);

    @GetMapping("/api/clientes/{id}")
    ResponseEntity<ClienteDTO> obtenerPorId(@PathVariable("id") Long id);

    @PostMapping("/api/clientes")
    ResponseEntity<ClienteDTO> crear(@RequestBody ClienteDTO clienteDTO);

    @PutMapping("/api/clientes/{id}")
    ResponseEntity<ClienteDTO> actualizar(
            @PathVariable("id") Long id,
            @RequestBody ClienteDTO clienteDTO);

    @PatchMapping("/api/clientes/{id}/estado")
    ResponseEntity<Void> cambiarEstado(
            @PathVariable("id") Long id,
            @RequestParam("activo") boolean activo);

    @DeleteMapping("/api/clientes/{id}")
    ResponseEntity<Void> eliminar(@PathVariable("id") Long id);

    // ============ MÉTODOS ADICIONALES ÚTILES PARA EL FRONTEND ============

    /**
     * Obtener cliente por email (útil para login/autenticación)
     * Nota: Este método asume que tienes un endpoint así en tu controller
     */
    @GetMapping("/api/clientes/email/{email}")
    ResponseEntity<ClienteDTO> obtenerPorEmail(@PathVariable("email") String email);

    /**
     * Verificar si un email ya está registrado
     */
    @GetMapping("/api/clientes/existe")
    ResponseEntity<Boolean> existePorEmail(@RequestParam("email") String email);
}