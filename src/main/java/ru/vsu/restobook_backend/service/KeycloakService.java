package ru.vsu.restobook_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import ru.vsu.restobook_backend.dto.ChangePasswordDto;
import ru.vsu.restobook_backend.dto.EmployeeDto;
import ru.vsu.restobook_backend.util.JwtUserUUIDConverter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class KeycloakService {

    private final JwtUserUUIDConverter jwtUserUUIDConverter;
    private final Keycloak keycloak;
    private final String clientId = "restobook-rest-api";

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

    public void createEmployee(EmployeeDto employeeDto) {
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();

        var userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setUsername(employeeDto.login());
        userRepresentation.setClientRoles(Map.of(clientId, List.of(employeeDto.role().get())));
        userRepresentation.setRealmRoles(List.of("default-roles-restaurant"));
        userRepresentation.setEmailVerified(false);

        var passwordCredential = new CredentialRepresentation();
        passwordCredential.setTemporary(false);
        passwordCredential.setType(CredentialRepresentation.PASSWORD);
        passwordCredential.setValue(employeeDto.password().get());
        userRepresentation.setCredentials(List.of(passwordCredential));

        try (var response = usersResource.create(userRepresentation)) {
            if (response.getStatus() == 201) {
                log.log(Level.INFO, "Employee successfully created in keycloak");
                var userOpt = getUserByLogin(employeeDto.login());
                if (userOpt.isPresent()) {
                    userRepresentation = userOpt.get();
                    assignRoleToUser(userRepresentation.getId(), employeeDto.role().get());
                }
            } else {
                log.log(Level.WARN, "Can't add employee to keycloak");
            }
        }
    }

    public void deleteEmployee(String employeeLogin) {
        UsersResource usersResource = keycloak.realm(realm).users();
        var userOpt = getUserByLogin(employeeLogin);
        if (userOpt.isPresent()) {
            var userRepresentation = userOpt.get();
            usersResource.delete(userRepresentation.getId());
        }
    }

    private void assignRoleToUser(String userId, String role) {
        UsersResource usersResource = keycloak.realm(realm).users();
        UserResource userResource = usersResource.get(userId);

        //getting client
        ClientRepresentation clientRepresentation = keycloak.realm(realm).clients().findAll().stream().filter(client -> client.getClientId().equals(clientId)).toList().get(0);
        ClientResource clientResource = keycloak.realm(realm).clients().get(clientRepresentation.getId());
        //getting role
        RoleRepresentation roleRepresentation = clientResource.roles().list().stream().filter(element -> element.getName().equals(role)).toList().get(0);
        //assigning to user
        userResource.roles().clientLevel(clientRepresentation.getId()).add(Collections.singletonList(roleRepresentation));
    }

    public Optional<UserRepresentation> getUserByLogin(String login) {
        return keycloak.realm(realm).users().search(login).stream()
                .filter(userRep -> userRep.getUsername().equals(login)).findFirst();
    }


}
