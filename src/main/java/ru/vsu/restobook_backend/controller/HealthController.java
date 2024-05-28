package ru.vsu.restobook_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class HealthController {
    @GetMapping("/health")
    public String health() {
        return "Hello from Spring boot keycloak HEALTH";
    }
}
