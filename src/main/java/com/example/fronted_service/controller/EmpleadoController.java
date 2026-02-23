package com.example.fronted_service.controller;

import com.bobax.dash.dto.PedidoDTO;
import com.bobax.dash.entity.Pedido;
import com.bobax.dash.enums.EstadoPedido;
import com.bobax.dash.repository.PedidoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/empleado")
public class EmpleadoController {

    private final PedidoRepository pedidoRepository;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public EmpleadoController(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

   
    @GetMapping
    public String menuEmpleado(Model model) {
        return "empleado/home"; 
    }

    
    @GetMapping("/pedidos")
    public String pedidosActivos(Model model) {
        List<PedidoDTO> pedidos = mapToDTOs(pedidoRepository.findAll());
        model.addAttribute("pedidos", pedidos);
        return "empleado/pedidos"; 
    }
    
    @PostMapping("/pedidos/{id}/estado")
    public String actualizarEstadoForm(@PathVariable Long id,
                                       @RequestParam("estado") String estado,
                                       RedirectAttributes redirectAttrs) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido == null) {
            redirectAttrs.addFlashAttribute("error", "Pedido no encontrado");
            return "redirect:/empleado/pedidos";
        }

        try {
            EstadoPedido nuevo = EstadoPedido.valueOf(estado);
            pedido.setEstado(nuevo);
            pedidoRepository.save(pedido);
            redirectAttrs.addFlashAttribute("success", "Estado actualizado correctamente");
        } catch (IllegalArgumentException ex) {
            redirectAttrs.addFlashAttribute("error", "Estado inválido");
        }

        return "redirect:/empleado/pedidos";
    }


    // 📊 Reportes rápidos
    @GetMapping("/reportes")
    public String reportes(Model model) {
        List<Pedido> todos = pedidoRepository.findAll();

        System.out.println("📊 Pedidos totales en BD: " + todos.size());

        long pendientes = todos.stream().filter(p -> p.getEstado() == EstadoPedido.PENDIENTE).count();
        long listos = todos.stream().filter(p -> p.getEstado() == EstadoPedido.LISTO).count();
        long entregados = todos.stream().filter(p -> p.getEstado() == EstadoPedido.ENTREGADO).count();

        model.addAttribute("totalPedidos", todos.size());
        model.addAttribute("pedidosPendientes", pendientes);
        model.addAttribute("pedidosListos", listos);
        model.addAttribute("pedidosEntregados", entregados);

        List<String> diasSemana = List.of("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");

        if (todos.isEmpty()) {
           
            model.addAttribute("diasSemana", diasSemana);
            model.addAttribute("pedidosPorDia", List.of(0, 0, 0, 0, 0, 0, 0));
        } else {
          
            long[] conteoPorDia = new long[7];
            todos.stream()
                    .filter(p -> p.getFechaCreacion() != null)
                    .forEach(p -> {
                        int dia = p.getFechaCreacion().getDayOfWeek().getValue(); // Lunes=1 ... Domingo=7
                        conteoPorDia[dia - 1]++;
                    });

            List<Long> pedidosPorDia = java.util.Arrays.stream(conteoPorDia)
                    .boxed()
                    .collect(Collectors.toList());

            model.addAttribute("diasSemana", diasSemana);
            model.addAttribute("pedidosPorDia", pedidosPorDia);
        }

        return "empleado/reportes";
    }


    @GetMapping("/api/pedidos")
    @ResponseBody
    public List<PedidoDTO> apiListarPedidos() {
        return mapToDTOs(pedidoRepository.findAll());
    }

    @PostMapping("/api/pedidos/{id}/estado")
    @ResponseBody
    public ResponseEntity<PedidoDTO> apiActualizarEstado(@PathVariable Long id, @RequestBody EstadoBody body) {
        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            EstadoPedido nuevo = EstadoPedido.valueOf(body.getEstado());
            pedido.setEstado(nuevo);
            pedidoRepository.save(pedido);
            return ResponseEntity.ok(mapToDTO(pedido));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

   
    private List<PedidoDTO> mapToDTOs(List<Pedido> pedidos) {
        return pedidos.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private PedidoDTO mapToDTO(Pedido p) {
        String cliente = p.getCliente() != null ? p.getCliente().getUsername() : "Anónimo";
        String fecha = p.getFechaCreacion() != null ? p.getFechaCreacion().format(dtf) : "";
        return new PedidoDTO(
                p.getId(),
                cliente,
                fecha,
                p.getTotal(),
                p.getEstado() != null ? p.getEstado().name() : ""
        );
    }

    // --- Clase auxiliar para actualizar estados ---
    public static class EstadoBody {
        private String estado;
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
    }
}
