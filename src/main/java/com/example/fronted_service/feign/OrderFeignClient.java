// fronted-service/src/main/java/com/example/fronted_service/feign/OrderFeignClient.java
package com.example.fronted_service.feign;

import com.example.fronted_service.dto.PedidoDTO;
import com.example.fronted_service.dto.ResumenPedidosDTO;
import com.example.fronted_service.dto.UserDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "order-service", url = "${order.service.url:http://localhost:8084}")
public interface OrderFeignClient {

    // ============ CRUD BÁSICO ============

    @GetMapping("/api/pedidos")
    ResponseEntity<List<PedidoDTO>> listarTodos();

    @GetMapping("/api/pedidos/{id}")
    ResponseEntity<PedidoDTO> buscarPorId(@PathVariable("id") Long id);

    @PostMapping("/api/pedidos")
    ResponseEntity<PedidoDTO> crear(@RequestBody PedidoDTO pedido);

    @PutMapping("/api/pedidos/{id}")
    ResponseEntity<PedidoDTO> actualizar(
            @PathVariable("id") Long id,
            @RequestBody PedidoDTO pedido);

    @DeleteMapping("/api/pedidos/{id}")
    ResponseEntity<Void> eliminar(@PathVariable("id") Long id);

    // ============ MÉTODOS ESPECÍFICOS ============

    @PatchMapping("/api/pedidos/{id}/estado")
    ResponseEntity<PedidoDTO> actualizarEstado(
            @PathVariable("id") Long id,
            @RequestParam("estado") String estado);

    @GetMapping("/api/pedidos/cliente/{clienteId}")
    ResponseEntity<List<PedidoDTO>> listarPorCliente(@PathVariable("clienteId") UserDTO usuario);

    @GetMapping("/api/pedidos/estado/{estado}")
    ResponseEntity<List<PedidoDTO>> listarPorEstado(@PathVariable("estado") String estado);

    @GetMapping("/api/pedidos/cliente/{clienteId}/estado/{estado}")
    ResponseEntity<List<PedidoDTO>> listarPorClienteYEstado(
            @PathVariable("clienteId") Long clienteId,
            @PathVariable("estado") String estado);

    @GetMapping("/api/pedidos/cliente/{clienteId}/resumen")
    ResponseEntity<ResumenPedidosDTO> obtenerResumen(@PathVariable("clienteId") Long clienteId);
}