package ru.vsu.restobook_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import ru.vsu.restobook_backend.dto.ChangePasswordDto;
import ru.vsu.restobook_backend.service.KeycloakService;

import java.security.Principal;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class DemoController {

    private final KeycloakService keycloakService;

    @GetMapping
    @PreAuthorize("hasAnyRole('restobook_user', 'restobook_admin')")
    public String hello(Principal principal) {
        return "Hello, %s, from Spring boot keycloak".formatted(principal.getName());
    }

    @GetMapping("/hello-2")
    @PreAuthorize("hasRole('restobook_admin')")
    public String hello2(Principal principal) {
        return "Hello, %s, from Spring boot keycloak ADMIN".formatted(principal.getName());
    }

    @GetMapping("/health")
    public String health() {
        return "Hello from Spring boot keycloak HEALTH";
    }

    @PostMapping("/changepass")
    @PreAuthorize("hasAnyRole('restobook_user', 'restobook_admin')")
    public HttpEntity<?> changePassword(
            @RequestBody ChangePasswordDto changePasswordDto,
            JwtAuthenticationToken principal) {
        keycloakService.changePassword(changePasswordDto, principal.getToken());
        return ResponseEntity.EMPTY;
    }
}
