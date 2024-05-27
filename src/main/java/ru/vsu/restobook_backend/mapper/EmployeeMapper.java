package ru.vsu.restobook_backend.mapper;

import org.springframework.stereotype.Service;
import ru.vsu.restobook_backend.dto.EmployeeDto;
import ru.vsu.restobook_backend.model.Employee;

import java.util.Optional;

@Service
public class EmployeeMapper {
    public EmployeeDto toDto(Employee employee) {
        return new EmployeeDto(
                Optional.of(employee.getId()),
                employee.getLogin(),
                employee.getSurname(),
                employee.getName(),
                Optional.empty(),
                Optional.empty(),
                Optional.ofNullable(employee.getPatronymic()),
                Optional.ofNullable(employee.getComment()),
                Optional.of(employee.isChangedPass()),
                Optional.of(employee.getRestaurant().getId())
        );
    }

    public EmployeeDto toDto(Employee employee, String role) {
        return new EmployeeDto(
                Optional.of(employee.getId()),
                employee.getLogin(),
                employee.getSurname(),
                employee.getName(),
                Optional.empty(),
                Optional.of(role),
                Optional.ofNullable(employee.getPatronymic()),
                Optional.ofNullable(employee.getComment()),
                Optional.of(employee.isChangedPass()),
                Optional.of(employee.getRestaurant().getId())
        );
    }
}
