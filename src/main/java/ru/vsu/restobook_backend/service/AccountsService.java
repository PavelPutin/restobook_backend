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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;

@Service
@AllArgsConstructor
@Log4j2
public class AccountsService {
    private final RestaurantsService restaurantsService;
    private final EmployeesRepository employeesRepository;
    private final KeycloakService keycloakService;


    public void createEmployee(int restaurantId, EmployeeDto employeeDto) {
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

    public List<Employee> getEmployees(int restaurantId) {
        var restaurant = restaurantsService.getById(restaurantId);
        return employeesRepository.findAllByRestaurant(restaurant);
    }

    public Employee getEmployeeById(int restaurantId, int employeeId, JwtAuthenticationToken principal) {
        // check if restaurant exists
        restaurantsService.getById(restaurantId);

        Optional<GrantedAuthority> authority = principal.getAuthorities().stream().findFirst();
        if (authority.isEmpty()) {
            throw new RestaurantForbiddenException(singletonList("User have not authority"));
        }

        if (authority.get().getAuthority().equals("ROLE_restobook_admin")) {
            var login = principal.getName();
            var admin = employeesRepository.findByLogin(login);
            if (admin.isPresent()) {
                var adminRestaurantId = admin.get().getRestaurant().getId();
                if (restaurantId == adminRestaurantId) {
                    Optional<Employee> employee = employeesRepository.findById(employeeId);
                    return employee.orElseThrow(() -> new NotFoundException(List.of("Employee not found with id " + employeeId)));
                } else {
                    throw new RestaurantForbiddenException(singletonList("You are not the admin of restaurant " + restaurantId));
                }
            }
        }

        Optional<Employee> employee = employeesRepository.findById(employeeId);
        return employee.orElseThrow(() -> new NotFoundException(List.of("Employee not found with id " + employeeId)));
    }
}
