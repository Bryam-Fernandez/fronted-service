package com.example.fronted_service.controller;

import com.example.fronted_service.dto.AuthRequestDTO;
import com.example.fronted_service.dto.AuthResponseDTO;
import com.example.fronted_service.feign.SecurityFeignClient;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private SecurityFeignClient securityFeignClient;

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("authRequest", new AuthRequestDTO());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute AuthRequestDTO authRequest,
                       HttpServletResponse response,
                       HttpSession session,
                       Model model) {

        try {
            // Llamar a TU security service (el que ya tienes)
            ResponseEntity<AuthResponseDTO> responseEntity = securityFeignClient.login(authRequest);
            AuthResponseDTO authResponse = responseEntity.getBody();

            if (authResponse != null && authResponse.getToken() != null) {
                // Guardar token en cookie
                Cookie jwtCookie = new Cookie("jwt-token", authResponse.getToken());
                jwtCookie.setHttpOnly(true);
                jwtCookie.setPath("/");
                jwtCookie.setMaxAge(24 * 60 * 60);
                response.addCookie(jwtCookie);

                // Guardar en sesión
                session.setAttribute("token", authResponse.getToken());
                session.setAttribute("username", authResponse.getUsername());
                session.setAttribute("roles", authResponse.getRoles());

                // Redirigir según rol
                if (authResponse.getRoles().contains("ROLE_ADMIN")) {
                    return "redirect:/admin/dashboard";
                } else {
                    return "redirect:/cliente";
                }
            }
        } catch (Exception e) {
            model.addAttribute("error", "Credenciales inválidas");
        }

        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response, HttpSession session) {
        Cookie jwtCookie = new Cookie("jwt-token", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);
        session.invalidate();
        return "redirect:/auth/login";
    }
}