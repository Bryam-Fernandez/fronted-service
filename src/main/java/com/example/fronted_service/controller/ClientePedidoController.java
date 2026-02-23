package com.example.fronted_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bobax.dash.entity.AppUser;
import com.bobax.dash.model.ItemCarrito;
import com.bobax.dash.repository.AppUserRepository;
import com.bobax.dash.service.ClienteCarritoService;
import com.bobax.dash.service.ClientePedidoService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cliente/pedido")
public class ClientePedidoController {

    @Autowired
    private AppUserRepository userRepo;

    @Autowired
    private ClientePedidoService pedidoService;
    
    @Autowired
    private ClienteCarritoService carritoService;

    @PostMapping("/pedido/confirmar")
    public String confirmarPedido(Authentication auth, HttpSession session, RedirectAttributes redirectAttrs) {
        AppUser usuario = getUsuarioActual(auth);
        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute("carrito");

        if (carrito == null || carrito.isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "❌ Tu carrito está vacío");
            return "redirect:/cliente/carrito";
        }

        boolean exito = pedidoService.confirmarPedido(usuario, carrito);

        if (exito) {
            session.removeAttribute("carrito"); 
            redirectAttrs.addFlashAttribute("mensaje", "✅ Pedido confirmado con éxito");
        } else {
            redirectAttrs.addFlashAttribute("error", "❌ No se pudo confirmar el pedido");
        }

        return "redirect:/cliente";
    }
    private AppUser getUsuarioActual(Authentication auth) {
        return userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }



}

