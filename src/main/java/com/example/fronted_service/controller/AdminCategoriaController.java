package com.example.fronted_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.fronted_service.dto.CategoriaDTO;
import com.example.fronted_service.feign.CategoriaClient;

@Controller
@RequestMapping("/admin/categorias")
public class AdminCategoriaController {
	
	  private final CategoriaClient categoriaClient;

	public AdminCategoriaController(CategoriaClient categoriaClient) {
		    this.categoriaClient = categoriaClient;
		  }
	
	 
	@GetMapping
	public String listar(Model modelo,
	                     @RequestParam(value = "q", required = false) String q,
	                     @RequestParam(value = "page", defaultValue = "0") int page,
	                     @RequestParam(value = "size", defaultValue = "10") int size,
	                     @RequestParam(value = "msg", required = false) String msg) {

	  var pagina = (q == null || q.isBlank())
	      ? categoriaClient.listarPaginado(page, size)
	      : categoriaClient.buscar(q, page, size);

	  modelo.addAttribute("pagina", pagina); 
	  modelo.addAttribute("q", q);
	  modelo.addAttribute("size", size);
	  modelo.addAttribute("msg", msg);
	  return "admin/categorias/lista";
	}

	  
	  
	  @GetMapping("/nuevo")
	  public String nuevo(Model modelo) {
	    modelo.addAttribute("categoria", new CategoriaDTO());
	    modelo.addAttribute("titulo", "Nueva categoría");
	    return "admin/categorias/form";
	  }
	  
	 
	  @PostMapping
	  public String crear(@ModelAttribute("categoria") CategoriaDTO categoria,
	                      RedirectAttributes attrs) {
		  categoriaClient.crear(categoria);
	    attrs.addFlashAttribute("msg", "Categoría creada correctamente");
	    return "redirect:/admin/categorias";
	  }
	  
	
	  @GetMapping("/{id}/editar")
	  public String editar(@PathVariable Long id, Model modelo, RedirectAttributes attrs) {
	      CategoriaDTO cat = categoriaClient.obtener(id); 
	      
	      if (cat == null) { 
	          attrs.addFlashAttribute("msg", "La categoría no existe");
	          return "redirect:/admin/categorias";
	      }

	      modelo.addAttribute("categoria", cat);
	      modelo.addAttribute("titulo", "Editar categoría");
	      return "admin/categorias/form";
	  }
	 
	  @PostMapping("/{id}")
	  public String actualizar(@PathVariable Long id,
	                           @ModelAttribute("categoria") CategoriaDTO datos,
	                           RedirectAttributes attrs) {
		  categoriaClient.actualizar(id, datos);
	    attrs.addFlashAttribute("msg", "Categoría actualizada");
	    return "redirect:/admin/categorias";
	  }
	  
	  
	  @PostMapping("/{id}/eliminar")
	  public String eliminar(@PathVariable Long id, RedirectAttributes attrs) {
		  categoriaClient.eliminar(id);
	    attrs.addFlashAttribute("msg", "Categoría eliminada");
	    return "redirect:/admin/categorias";
	  }
	
}
