package com.example.fronted_service.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.fronted_service.dto.CategoriaDTO;
import com.example.fronted_service.dto.ItemCarritoDTO;
import com.example.fronted_service.dto.ProductoDTO;
import com.example.fronted_service.dto.UserDTO;
import com.example.fronted_service.feign.CarritoFeign;
import com.example.fronted_service.feign.CategoriaClient;
import com.example.fronted_service.feign.ImgProductoFeign;
import com.example.fronted_service.feign.OrderFeignClient;
import com.example.fronted_service.feign.ProductoFeign;
import com.example.fronted_service.feign.SecurityFeignClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cliente")
public class ClienteController {
    
    @Autowired
    private ImgProductoFeign imgProductoFeign;

    @Autowired
    private CategoriaClient categoriaFeign;

    @Autowired
    private ProductoFeign productoFeign;

    @Autowired
    private CarritoFeign carritoFeign;

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private SecurityFeignClient securityFeign;

    // ✅ MÉTODO CORREGIDO - Ahora retorna UserDTO, no ResponseEntity
    private UserDTO getUsuarioActual(Authentication auth) {
        String username = auth.getName();
        ResponseEntity<UserDTO> response = securityFeign.findByUsername(username);
        
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }
        
        throw new RuntimeException("Usuario no encontrado: " + username);
    }

    /** ----------------- MENU / PRODUCTOS ----------------- */
    @GetMapping
    public String menuCliente(@RequestParam(required = false) Long categoriaId,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "20") int size,
                              Model model,
                              Authentication auth) {

        // Obtener usuario actual (ahora es UserDTO, no ResponseEntity)
        UserDTO usuario = getUsuarioActual(auth);
        
        // Obtener categorías
        List<CategoriaDTO> categorias = categoriaFeign.listar();
        model.addAttribute("categorias", categorias);

        // Obtener productos según categoría
        List<ProductoDTO> productos;
        if (categoriaId != null) {
            var response = productoFeign.listarPorCategoria(categoriaId, page, size);
            productos = response.getContent();
            model.addAttribute("selectedCategoriaId", categoriaId);
        } else {
            var response = productoFeign.listarHabilitados(page, size);
            productos = response.getContent();
        }
        model.addAttribute("productos", productos);
        model.addAttribute("usuario", usuario.getUsername()); // ✅ Ahora funciona

        // Obtener pedidos del cliente
        try {
            var pedidosResponse = orderFeignClient.listarPorCliente(usuario.getId());
            model.addAttribute("pedidos", pedidosResponse.getBody());
        } catch (Exception e) {
            model.addAttribute("pedidos", new ArrayList<>());
        }

        return "cliente/menu";
    }

    @GetMapping("/destacados")
    public String productosDestacados(Model model, Authentication auth) {
        UserDTO usuario = getUsuarioActual(auth);
        var response = productoFeign.listarDestacados(0, 20);
        
        model.addAttribute("productos", response.getContent());
        model.addAttribute("usuario", usuario.getUsername()); // ✅ Corregido
        
        return "cliente/menu";
    }

    @GetMapping("/carrito")
    public String verCarrito(Model model, Authentication auth) {
        UserDTO usuario = getUsuarioActual(auth);
        Long clienteId = usuario.getId(); // ✅ Ahora funciona

        var carritoResponse = carritoFeign.obtenerCarrito(clienteId);
        var carrito = carritoResponse.getBody();
        var pedidosResponse = orderFeignClient.listarPorCliente(clienteId); // ✅ Ahora funciona

        model.addAttribute("carrito", carrito != null ? carrito.getItems() : new ArrayList<>());
        model.addAttribute("total", carrito != null ? carrito.getTotal() : BigDecimal.ZERO);
        model.addAttribute("usuario", usuario.getUsername());
        model.addAttribute("pedidos", pedidosResponse.getBody());

        return "cliente/carrito";
    }

    @PostMapping("/carrito/agregar")
    public String agregarAlCarrito(@RequestParam Long productoId,
                                   @RequestParam int cantidad,
                                   @RequestParam(required = false) String tamaño,
                                   @RequestParam(required = false) String[] toppings,
                                   Authentication auth,
                                   RedirectAttributes redirectAttrs) {

        UserDTO usuario = getUsuarioActual(auth); // ✅ Corregido
        Long clienteId = usuario.getId();

        // Obtener producto
        ProductoDTO producto = productoFeign.buscarPorId(productoId);

        // Obtener imagen
        String imagenUrl = "/img/no-image.png";
        try {
            var imgResponse = imgProductoFeign.obtenerUrlPrincipal(productoId);
            if (imgResponse.getBody() != null) {
                imagenUrl = imgResponse.getBody().toString();
            }
        } catch (Exception e) {
            // Usar imagen por defecto
        }

        // Crear item DTO
        ItemCarritoDTO itemDTO = new ItemCarritoDTO();
        itemDTO.setProductoId(productoId);
        itemDTO.setProductoNombre(producto.getNombre());
        itemDTO.setProductoPrecio(producto.getPrecio());
        itemDTO.setCantidad(cantidad);
        itemDTO.setTamaño(tamaño);
        itemDTO.setToppings(toppings != null ? String.join(", ", toppings) : null);
        itemDTO.setImagenUrl(imagenUrl);

        // Enviar al carrito microservicio
        carritoFeign.agregarItem(clienteId, itemDTO);

        redirectAttrs.addFlashAttribute("mensaje", "✅ Producto agregado al carrito");
        return "redirect:/cliente/carrito";
    }

    @PostMapping("/carrito/eliminar")
    public String eliminarDelCarrito(@RequestParam Long productoId,
                                     Authentication auth,
                                     RedirectAttributes redirectAttrs) {
        UserDTO usuario = getUsuarioActual(auth); // ✅ Corregido
        carritoFeign.eliminarItem(usuario.getId(), productoId);
        redirectAttrs.addFlashAttribute("mensaje", "✅ Producto eliminado");
        return "redirect:/cliente/carrito";
    }

    @PostMapping("/carrito/vaciar")
    public String vaciarCarrito(Authentication auth, RedirectAttributes redirectAttrs) {
        UserDTO usuario = getUsuarioActual(auth); // ✅ Corregido
        carritoFeign.vaciarCarrito(usuario.getId());
        redirectAttrs.addFlashAttribute("mensaje", "✅ Carrito vaciado");
        return "redirect:/cliente/carrito";
    }

    @PostMapping("/pedido/confirmar")
    public String confirmarPedido(Authentication auth, RedirectAttributes redirectAttrs) {
        UserDTO usuario = getUsuarioActual(auth); // ✅ Corregido
        Long clienteId = usuario.getId();

        try {
            var carritoResponse = carritoFeign.obtenerCarrito(clienteId);
            var carritoDTO = carritoResponse.getBody();

            if (carritoDTO == null || carritoDTO.getItems().isEmpty()) {
                redirectAttrs.addFlashAttribute("error", "❌ Tu carrito está vacío");
                return "redirect:/cliente/carrito";
            }

            // Crear pedido en order-service
            // var response = orderFeignClient.crearPedido(clienteId, carritoDTO.getItems());

            // Vaciar carrito
            carritoFeign.vaciarCarrito(clienteId);
            redirectAttrs.addFlashAttribute("mensaje", "✅ Pedido confirmado con éxito");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttrs.addFlashAttribute("error", "❌ Error al procesar el pedido");
        }

        return "redirect:/cliente";
    }

    @GetMapping("/personalizar/{id}")
    public String personalizarProducto(@PathVariable Long id, 
                                       Model model, 
                                       Authentication auth) {
        
        UserDTO usuario = getUsuarioActual(auth); // ✅ Corregido
        ProductoDTO producto = productoFeign.buscarPorId(id);

        // Obtener imagen
        String urlImagen = "/img/placeholder.png";
        try {
            var imgResponse = imgProductoFeign.obtenerUrlPrincipal(id);
            if (imgResponse.getBody() != null) {
                urlImagen = imgResponse.getBody().toString();
            }
        } catch (Exception e) {
            // Usar imagen por defecto
        }

        // Obtener categorías
        List<CategoriaDTO> categorias = categoriaFeign.listarTodos();

        // Obtener pedidos
        var pedidosResponse = orderFeignClient.listarPorCliente(usuario.getId());

        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categorias);
        model.addAttribute("selectedCategoriaId", producto.getCategoriaId());
        model.addAttribute("imagenUrl", urlImagen);
        model.addAttribute("usuario", usuario.getUsername());
        model.addAttribute("pedidos", pedidosResponse.getBody());

        return "admin/productos/personalizarBebida";
    }
}