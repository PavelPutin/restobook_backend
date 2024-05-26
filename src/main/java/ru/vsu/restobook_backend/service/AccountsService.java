package ru.vsu.restobook_backend.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.stereotype.Service;
import ru.vsu.restobook_backend.dto.EmployeeDto;
import ru.vsu.restobook_backend.model.Employee;
import ru.vsu.restobook_backend.repository.EmployeesRepository;

import java.util.ArrayList;
import java.util.List;

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
}
