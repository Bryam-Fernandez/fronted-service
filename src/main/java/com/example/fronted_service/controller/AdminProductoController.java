package com.example.fronted_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.fronted_service.dto.PageDTO;
import com.example.fronted_service.dto.ProductoDTO;
import com.example.fronted_service.feign.CategoriaClient;
import com.example.fronted_service.feign.ImgProductoFeign;
import com.example.fronted_service.feign.ProductoFeign;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/productos")
public class AdminProductoController {

  private final ProductoFeign productoFeign;
  private final CategoriaClient categoriaClient;
  private final ImgProductoFeign imgProductoFeign;

  public AdminProductoController(ProductoFeign productoFeign,
		  CategoriaClient categoriaClient,
		  ImgProductoFeign imgProductoFeign) {
    this.productoFeign = productoFeign;
    this.categoriaClient = categoriaClient;
    this.imgProductoFeign = imgProductoFeign;
  }

  @GetMapping
  public String listar(Model modelo,
                       @RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "10") int size,
                       @RequestParam(value = "msg", required = false) String msg) {

      PageDTO<ProductoDTO> pagina = (q == null || q.isBlank())
          ? productoFeign.listarPaginado(page, size)
          : productoFeign.buscar(q, page, size);

      Map<Long, Object> thumbnails = pagina.getContent().stream()
          .collect(Collectors.toMap(
              ProductoDTO::getId,
              p -> imgProductoFeign.obtenerUrlPrincipal(p.getId())
          ));

      modelo.addAttribute("pagina", pagina);
      modelo.addAttribute("thumbnails", thumbnails);
      modelo.addAttribute("q", q);
      modelo.addAttribute("size", size);
      modelo.addAttribute("msg", msg);

      return "admin/productos/lista";
  }

  
  @GetMapping("/nuevo")
  public String nuevo(Model modelo) {
    modelo.addAttribute("producto", new ProductoDTO());
    modelo.addAttribute("categorias", categoriaClient.listar());
    modelo.addAttribute("titulo", "Nuevo producto");
    return "admin/productos/form";
  }

  // CREAR
  @PostMapping
  public String crear(@ModelAttribute("producto") ProductoDTO producto,
                      @RequestParam("categoriaId") Long categoriaId,
                      RedirectAttributes attrs) {
	  productoFeign.crear(producto, categoriaId);
    attrs.addFlashAttribute("msg", "Producto creado correctamente");
    return "redirect:/admin/productos";
  }


  @GetMapping("/{id}/editar")
  public String editar(@PathVariable Long id, Model modelo, RedirectAttributes attrs) {
      // Buscar producto
      ProductoDTO producto = productoFeign.buscarPorId(id);

      // Validar si existe
      if (producto == null) {
          attrs.addFlashAttribute("msg", "El producto no existe");
          return "redirect:/admin/productos";
      }

      // Agregar datos al modelo
      modelo.addAttribute("producto", producto);
      modelo.addAttribute("categorias", categoriaClient.listar());
      modelo.addAttribute("titulo", "Editar producto");

      // URL principal de la imagen
      modelo.addAttribute("urlPrincipal", imgProductoFeign.obtenerUrlPrincipal(id));

      return "admin/productos/form";
  }

  
  @PostMapping("/{id}")
  public String actualizar(@PathVariable Long id,
                           @ModelAttribute("producto") ProductoDTO datos,
                           @RequestParam("categoriaId") Long categoriaId,
                           RedirectAttributes attrs) {
	  productoFeign.actualizar(id, datos, categoriaId);
    attrs.addFlashAttribute("msg", "Producto actualizado");
    return "redirect:/admin/productos";
  }

 
  @PostMapping("/{id}/estado")
  public String cambiarEstado(@PathVariable Long id,
                              @RequestParam("habilitado") boolean habilitado,
                              RedirectAttributes attrs) {
	  productoFeign.habilitar(id, habilitado);
    attrs.addFlashAttribute("msg", "Estado actualizado");
    return "redirect:/admin/productos";
  }

  @PostMapping("/{id}/eliminar")
  public String eliminar(@PathVariable Long id, RedirectAttributes attrs) {
	  productoFeign.eliminar(id);
    attrs.addFlashAttribute("msg", "Producto eliminado");
    return "redirect:/admin/productos";
  }
}
