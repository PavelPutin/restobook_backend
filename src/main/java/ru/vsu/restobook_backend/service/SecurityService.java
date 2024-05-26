package ru.vsu.restobook_backend.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

@Service
@AllArgsConstructor
public class SecurityService {
    private final RestaurantsService restaurantsService;
    private final AccountsService accountsService;

    public boolean isRestaurantAdmin(int restaurantId, JwtAuthenticationToken principal) {
        restaurantsService.getById(restaurantId);
        Set<String> roles = getRolesFromJwtAuthentication(principal);

        if (roles.contains("ROLE_restobook_admin")) {
            var login = principal.getName();
            var admin = accountsService.getEmployeeByLogin(login);
            var adminRestaurantId = admin.getRestaurant().getId();
            return restaurantId == adminRestaurantId;
        }
        return false;
    }

    public boolean isVendorAdmin(JwtAuthenticationToken principal) {
        Set<String> roles = getRolesFromJwtAuthentication(principal);
        return roles.contains("ROLE_restobook_user");
    }

    public boolean isThemSelfUser(int employeeId, JwtAuthenticationToken principal) {
        Set<String> roles = getRolesFromJwtAuthentication(principal);

        if (roles.contains("ROLE_restobook_user")) {
            var login = principal.getName();
            var user = accountsService.getEmployeeByLogin(login);
            return user.getId() == employeeId;
        }
        return false;
    }

    private Set<String> getRolesFromJwtAuthentication(JwtAuthenticationToken principal) {
        return principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }
}
