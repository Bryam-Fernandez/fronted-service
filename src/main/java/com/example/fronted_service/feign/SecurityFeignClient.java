package com.example.fronted_service.feign;

import com.example.fronted_service.dto.AuthRequestDTO;
import com.example.fronted_service.dto.AuthResponseDTO;
import com.example.fronted_service.dto.UserDTO;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "security-service", url = "${security.service.url:http://localhost:8085}")
public interface SecurityFeignClient {

    @PostMapping("/api/auth/login")
    ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request);

    @GetMapping("/api/verificacion")
    ResponseEntity<String> verificarToken(@RequestHeader("Authorization") String token);
    
    @GetMapping("/api/auth/usuarios/buscar")
    ResponseEntity<UserDTO> findByUsername(@RequestParam("username") String username);
    default Optional<UserDTO> findByUsernameOptional(String username) {
        try {
            ResponseEntity<UserDTO> response = findByUsername(username);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Optional.of(response.getBody());
            }
        } catch (Exception e) {
			System.out.println("Error al buscar usuario: " + e.getMessage());
        }
        return Optional.empty();
    }
}