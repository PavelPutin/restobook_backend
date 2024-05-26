package ru.vsu.restobook_backend.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.vsu.restobook_backend.dto.ChangePasswordDto;
import ru.vsu.restobook_backend.service.KeycloakService;

@Controller
@RequestMapping("/auth")
@AllArgsConstructor
@Log4j2
public class AuthController {

    private final KeycloakService keycloakService;

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto changePasswordDto, JwtAuthenticationToken principal) {
        try {
            keycloakService.changePassword(changePasswordDto, principal.getToken());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.log(Level.ERROR, e.getMessage());
            log.log(Level.ERROR, e.getStackTrace());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
