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
import ru.vsu.restobook_backend.service.AccountsService;
import ru.vsu.restobook_backend.service.KeycloakService;

@Controller
@RequestMapping("/auth")
@AllArgsConstructor
@Log4j2
public class AuthController {

    private final KeycloakService keycloakService;
    private final AccountsService accountsService;

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto changePasswordDto, JwtAuthenticationToken principal) {
        try {
            accountsService.changePassword(changePasswordDto, principal);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to change password for {}", principal.getName(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
