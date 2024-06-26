package ru.vsu.restobook_backend.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.vsu.restobook_backend.dto.ChangePasswordDto;
import ru.vsu.restobook_backend.dto.EmployeeDto;
import ru.vsu.restobook_backend.mapper.EmployeeMapper;
import ru.vsu.restobook_backend.model.Employee;
import ru.vsu.restobook_backend.repository.AccountsRepository;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

@Service
@AllArgsConstructor
@Log4j2
public class AccountsService {
    private final RestaurantsService restaurantsService;
    private final AccountsRepository accountsRepository;
    private final SecurityService securityService;
    private final KeycloakService keycloakService;
    private final EmployeeMapper employeeMapper;

    public Employee createEmployee(int restaurantId, EmployeeDto employeeDto, JwtAuthenticationToken principal) {
        var restaurant = restaurantsService.getById(restaurantId);

        List<String> validationErrors = new ArrayList<>();
        boolean loginUnique =
                accountsRepository.findByLogin(employeeDto.login()).isEmpty() &&
                keycloakService.getUserByLogin(employeeDto.login()).isEmpty();
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

        if (securityService.isVendorAdmin(principal) && !employeeDto.role().get().equals("restobook_admin")) {
            throw new RestaurantForbiddenException(singletonList("You are allowed to create only restobook_admin"));
        }

        if (!securityService.isRestaurantAdmin(restaurantId, principal) && !securityService.isVendorAdmin(principal)) {
            throw new RestaurantForbiddenException(singletonList("You are not the admin of restaurant " + restaurantId));
        }

        var employee = new Employee();
        employee.setLogin(employeeDto.login().toLowerCase());
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
        employee = accountsRepository.save(employee);
        try {
            keycloakService.createEmployee(employeeDto);
            return employee;
        } catch (RuntimeException e) {
            accountsRepository.delete(employee);
            throw e;
        }
    }

    public List<Employee> getEmployees(int restaurantId, JwtAuthenticationToken principal) {
        var restaurant = restaurantsService.getById(restaurantId);

        if (securityService.isVendorAdmin(principal)) {
            return accountsRepository.findAllByRestaurant(restaurant);
        }

        if (securityService.isRestaurantAdmin(restaurantId, principal)) {
            return accountsRepository.findAllByRestaurant(restaurant);
        } else {
            throw new RestaurantForbiddenException(singletonList("You are not allowed to get users"));
        }
    }

    public Employee getEmployeeById(int restaurantId, int employeeId, JwtAuthenticationToken principal) {
        // check if restaurant exists
        restaurantsService.getById(restaurantId);

        Set<String> roles = getRolesFromJwtAuthentication(principal);

        if (securityService.isVendorAdmin(principal)) {
            return findByIdWithException(employeeId);
        }

        if (securityService.isRestaurantAdmin(restaurantId, principal)) {
            return findByIdWithException(employeeId);
        }

        if (securityService.isThemSelfUser(employeeId, principal)) {
            return findByIdWithException(employeeId);
        }
        throw new RestaurantForbiddenException(singletonList("You are not allowed to get user " + employeeId));
    }

    private Employee findByIdWithException(int employeeId) {
        Optional<Employee> employee = accountsRepository.findById(employeeId);
        return employee.orElseThrow(() -> new NotFoundException(List.of("Employee not found with id " + employeeId)));
    }

    private Set<String> getRolesFromJwtAuthentication(JwtAuthenticationToken principal) {
        return principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }

    public Employee updateEmployee(int restaurantId, int employeeId, EmployeeDto employeeDto, JwtAuthenticationToken principal) {
        restaurantsService.getById(restaurantId);

        if (!securityService.isRestaurantAdmin(restaurantId, principal)) {
            throw new RestaurantForbiddenException(singletonList("You are not the admin of restaurant " + restaurantId));
        }

        var employee = findByIdWithException(employeeId);
        employee.setName(employeeDto.name());
        employee.setSurname(employeeDto.surname());
        employee.setPatronymic(employeeDto.patronymic().orElse(null));
        employee.setComment(employeeDto.comment().orElse(null));
        accountsRepository.save(employee);
        return employee;
    }

    public void deleteEmployee(int restaurantId, int employeeId, JwtAuthenticationToken principal) {
        restaurantsService.getById(restaurantId);

        if (!securityService.isRestaurantAdmin(restaurantId, principal)) {
            throw new RestaurantForbiddenException(singletonList("You are not the admin of restaurant " + restaurantId));
        }

        var toDelete = findByIdWithException(employeeId);
        try {
            keycloakService.deleteEmployee(toDelete.getLogin());
            accountsRepository.delete(toDelete);
        } catch (RuntimeException e) {
            log.log(Level.ERROR, "Can't delete user " + employeeId);
        }
    }

    public void changePassword(ChangePasswordDto changePasswordDto, JwtAuthenticationToken principal) {
        var employee = getEmployeeByLogin(principal.getName());
        keycloakService.changePassword(changePasswordDto, principal.getToken());
        employee.setChangedPass(true);
        accountsRepository.save(employee);
    }

    public Employee getEmployeeByLogin(String login) {
        return accountsRepository.findByLogin(login).orElseThrow(() ->
                new NotFoundException(singletonList("Employee not found with login " + login)));
    }

    public EmployeeDto getAuthenticatedUser(JwtAuthenticationToken principal) {
        Employee employee = getEmployeeByLogin(principal.getName());

        EmployeeDto result;
        if (securityService.isRestaurantAdmin(employee.getRestaurant().getId(), principal)) {
            result = employeeMapper.toDto(employee, "ROLE_restobook_admin");
        } else {
            result = employeeMapper.toDto(employee, "ROLE_restobook_user");
        }
        return result;
    }
}
