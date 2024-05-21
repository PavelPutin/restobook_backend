package ru.vsu.restobook_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class DemoController {

    @GetMapping
    public String hello() {
        return "Hello from Spring boot keycloak";
    }

    @GetMapping("/hello-2")
    public String hello2() {
        return "Hello from Spring boot keycloak ADMIN";
    }

    @GetMapping("/health")
    public String health() {
        return "Hello from Spring boot keycloak HEALTH";
    }
}
