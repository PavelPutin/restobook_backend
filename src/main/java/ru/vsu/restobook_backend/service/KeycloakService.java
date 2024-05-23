package ru.vsu.restobook_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import ru.vsu.restobook_backend.dto.ChangePasswordDto;
import ru.vsu.restobook_backend.util.JwtUserUUIDConverter;

@Service
@RequiredArgsConstructor
@Log4j2
public class KeycloakService {

    private final JwtUserUUIDConverter jwtUserUUIDConverter;
    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public void changePassword(ChangePasswordDto changePasswordDto, Jwt jwt) {
        log.log(Level.INFO, "Start changing password");
        var userId = jwtUserUUIDConverter.convert(jwt);

        var realmResource = keycloak.realm(realm);
        var usersResource = realmResource.users();
        var userResource = usersResource.get(String.valueOf(userId));

        var passwordCredential = new CredentialRepresentation();
        passwordCredential.setTemporary(false);
        passwordCredential.setType(CredentialRepresentation.PASSWORD);
        passwordCredential.setValue(changePasswordDto.newPassword());

        userResource.resetPassword(passwordCredential);
        log.log(Level.INFO, "End changing password");
    }
}
