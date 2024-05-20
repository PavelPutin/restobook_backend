package ru.vsu.restobook_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/demo")
public class DemoController {

    @GetMapping
    public String hello() {
        return "Hello from Spring boot keycloak";
    }

    @GetMapping("/hello-2")
    public String hello2() {
        return "Hello from Spring boot keycloak ADMIN";
    }
}
