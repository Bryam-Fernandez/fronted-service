package com.example.fronted_service.dto;

import java.util.Set;

public class AuthResponseDTO {
    private String token;
    private String tokenType;
    private String username;
    private Set<String> roles;

    public AuthResponseDTO() {}

    public AuthResponseDTO(String token, String tokenType, String username, Set<String> roles) {
        this.token = token;
        this.tokenType = tokenType;
        this.username = username;
        this.roles = roles;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}