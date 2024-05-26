package ru.vsu.restobook_backend.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.vsu.restobook_backend.dto.EmployeeDto;
import ru.vsu.restobook_backend.model.Employee;
import ru.vsu.restobook_backend.model.Restaurant;
import ru.vsu.restobook_backend.repository.EmployeesRepository;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

@Service
@AllArgsConstructor
@Log4j2
public class AccountsService {
    private final RestaurantsService restaurantsService;
    private final EmployeesRepository employeesRepository;
    private final KeycloakService keycloakService;


    public void createEmployee(int restaurantId, EmployeeDto employeeDto, JwtAuthenticationToken principal) {
        var restaurant = restaurantsService.getById(restaurantId);

        List<String> validationErrors = new ArrayList<>();
        boolean loginUnique = employeesRepository.findByLogin(employeeDto.login()).isEmpty();
        if (!loginUnique) {
            validationErrors.add("Login must be unique");
        }

        if (employeeDto.password().isEmpty()) {
            validationErrors.add("Password is required for employee creation");
        }

        if (employeeDto.role().isEmpty()) {
            validationErrors.add("Role is required for employee creation");
        }

        if (!validationErrors.isEmpty()) {
            log.log(Level.INFO, "Can't create employee");
            throw new ValidationError(validationErrors);
        }

        Set<String> roles = getRolesFromJwtAuthentication(principal);

        if (roles.contains("ROLE_vendor_admin") && !employeeDto.role().get().equals("restobook_admin")) {
            throw new RestaurantForbiddenException(singletonList("You are allowed to create only restobook_admin"));
        }

        if (roles.contains("ROLE_restobook_admin")) {
            var login = principal.getName();
            var admin = employeesRepository.findByLogin(login);
            if (admin.isPresent()) {
                var adminRestaurantId = admin.get().getRestaurant().getId();
                if (restaurantId != adminRestaurantId) {
                    throw new RestaurantForbiddenException(singletonList("You are not the admin of restaurant " + restaurantId));
                }
            }
        }

        if (roles.contains("ROLE_restobook_user")) {
            throw new RestaurantForbiddenException(singletonList("You are not allowed to create users"));
        }

        var employee = new Employee();
        employee.setLogin(employeeDto.login());
        employee.setSurname(employeeDto.surname());
        if (employeeDto.patronymic().isPresent()) {
            employee.setPatronymic(employeeDto.patronymic().get());
        }
        employee.setName(employeeDto.name());
        if (employeeDto.comment().isPresent()) {
            employee.setComment(employeeDto.comment().get());
        }
        employee.setChangedPass(false);
        employee.setRestaurant(restaurant);
        employee = employeesRepository.save(employee);
        try {
            keycloakService.createEmployee(employeeDto);
        } catch (RuntimeException e) {
            employeesRepository.delete(employee);
            throw e;
        }
    }

    public List<Employee> getEmployees(int restaurantId, JwtAuthenticationToken principal) {
        var restaurant = restaurantsService.getById(restaurantId);

        Set<String> roles = getRolesFromJwtAuthentication(principal);

        if (roles.contains("ROLE_vendor_admin")) {
            return employeesRepository.findAllByRestaurant(restaurant);
        }

        if (roles.contains("ROLE_restobook_admin")) {
            var login = principal.getName();
            var admin = employeesRepository.findByLogin(login);
            if (admin.isPresent()) {
                var adminRestaurantId = admin.get().getRestaurant().getId();
                if (restaurantId == adminRestaurantId) {
                    return employeesRepository.findAllByRestaurant(restaurant);
                } else {
                    throw new RestaurantForbiddenException(singletonList("You are not the admin of restaurant " + restaurantId));
                }
            }
        }

        throw new RestaurantForbiddenException(singletonList("You are not allowed to get users"));
    }

    public Employee getEmployeeById(int restaurantId, int employeeId, JwtAuthenticationToken principal) {
        // check if restaurant exists
        restaurantsService.getById(restaurantId);

        Set<String> roles = getRolesFromJwtAuthentication(principal);

        if (roles.contains("ROLE_vendor_admin")) {
            return findByIdWithException(employeeId);
        }

        if (roles.contains("ROLE_restobook_admin")) {
            var login = principal.getName();
            var admin = employeesRepository.findByLogin(login);
            if (admin.isPresent()) {
                var adminRestaurantId = admin.get().getRestaurant().getId();
                if (restaurantId == adminRestaurantId) {
                    return findByIdWithException(employeeId);
                } else {
                    throw new RestaurantForbiddenException(singletonList("You are not the admin of restaurant " + restaurantId));
                }
            }
        }

        if (roles.contains("ROLE_restobook_user")) {
            var login = principal.getName();
            var userOpt = employeesRepository.findByLogin(login);
            if (userOpt.isPresent()) {
                var user = userOpt.get();
                if (user.getId() == employeeId) {
                    return findByIdWithException(employeeId);
                } else {
                    throw new RestaurantForbiddenException(singletonList("You are not this user"));
                }
            }
        }

        throw new RestaurantForbiddenException(singletonList("You are not allowed to get user"));
    }

    public Employee getEmployeeByLogin(String login) {
        return employeesRepository.findByLogin(login).orElseThrow(() ->
                new NotFoundException(singletonList("Employee not found with login " + login)));
    }

    private Employee findByIdWithException(int employeeId) {
        Optional<Employee> employee = employeesRepository.findById(employeeId);
        return employee.orElseThrow(() -> new NotFoundException(List.of("Employee not found with id " + employeeId)));
    }

    private Set<String> getRolesFromJwtAuthentication(JwtAuthenticationToken principal) {
        return principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }

    public void updateEmployee(int restaurantId, int employeeId, EmployeeDto employeeDto, JwtAuthenticationToken principal) {
        var restaurant = restaurantsService.getById(restaurantId);

        Set<String> roles = getRolesFromJwtAuthentication(principal);

        if (roles.contains("ROLE_restobook_admin")) {
            var login = principal.getName();
            var admin = employeesRepository.findByLogin(login);
            if (admin.isPresent()) {
                var adminRestaurantId = admin.get().getRestaurant().getId();
                if (restaurantId != adminRestaurantId) {
                    throw new RestaurantForbiddenException(singletonList("You are not the admin of restaurant " + restaurantId));
                }
            }
        }

        if (roles.contains("ROLE_restobook_user")) {
            throw new RestaurantForbiddenException(singletonList("You are not allowed to create users"));
        }


        var employee = findByIdWithException(employeeId);
        employee.setName(employeeDto.name());
        employee.setSurname(employeeDto.surname());
        if (employeeDto.patronymic().isPresent()) {
            employee.setPatronymic(employeeDto.patronymic().get());
        }
        if (employeeDto.comment().isPresent()) {
            employee.setComment(employeeDto.comment().get());
        }
        employeesRepository.save(employee);
    }

    public void deleteEmployee(int restaurantId, int employeeId, JwtAuthenticationToken principal) {
        restaurantsService.getById(restaurantId);
        Set<String> roles = getRolesFromJwtAuthentication(principal);

        if (roles.contains("ROLE_restobook_admin")) {
            var login = principal.getName();
            var admin = employeesRepository.findByLogin(login);
            if (admin.isPresent()) {
                var adminRestaurantId = admin.get().getRestaurant().getId();
                if (restaurantId != adminRestaurantId) {
                    throw new RestaurantForbiddenException(singletonList("You are not the admin of restaurant " + restaurantId));
                }
            }
        }

        var toDelete = findByIdWithException(employeeId);
        try {
            keycloakService.deleteEmployee(toDelete.getLogin());
            employeesRepository.delete(toDelete);
        } catch (RuntimeException e) {
            log.log(Level.ERROR, "Can't delete user " + employeeId);
        }
    }
}
