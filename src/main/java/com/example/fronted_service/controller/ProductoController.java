package com.example.fronted_service.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bobax.dash.entity.Producto;
import com.bobax.dash.model.ItemCarrito;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/producto")
public class ProductoController {
	
	@Autowired
    private com.bobax.dash.service.ProductoService productoService;

	@GetMapping("/{id}/detalles")
	public String verDetalles(@PathVariable Long id, Model model) {
	    Producto producto = productoService.buscarPorId(id)
	        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
	    model.addAttribute("producto", producto);
	    return "producto-detalle"; 
	}



}
