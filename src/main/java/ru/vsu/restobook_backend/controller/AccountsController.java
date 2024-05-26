package ru.vsu.restobook_backend.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.vsu.restobook_backend.dto.EmployeeDto;
import ru.vsu.restobook_backend.dto.ErrorDto;
import ru.vsu.restobook_backend.mapper.EmployeeMapper;
import ru.vsu.restobook_backend.model.Employee;
import ru.vsu.restobook_backend.service.AccountsService;
import ru.vsu.restobook_backend.service.NotFoundException;
import ru.vsu.restobook_backend.service.ValidationError;

import java.time.Instant;
import java.util.List;

@Controller
@RequestMapping("/restaurant/{restaurantId}/employee")
@AllArgsConstructor
public class AccountsController {
    private final AccountsService accountsService;
    private final EmployeeMapper employeeMapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('vendor_admin', 'restobook_admin')")
    public ResponseEntity<?> createEmployee(@PathVariable int restaurantId, @RequestBody EmployeeDto employeeDto) {
        try {
            accountsService.createEmployee(restaurantId, employeeDto);
            return ResponseEntity.ok().build();
        } catch (ValidationError e) {
            return ResponseEntity.badRequest().body(new ErrorDto(Instant.now(), e.getErrors()));
        } catch (NotFoundException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('vendor_admin', 'restobook_admin')")
    public ResponseEntity<?> getAllEmployeesByRestaurantId(@PathVariable int restaurantId) {
        try {
            List<Employee> employees = accountsService.getEmployees(restaurantId);
            List<EmployeeDto> result = employees.stream().map(employeeMapper::toDto).toList();
            return ResponseEntity.ok().body(result);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.NOT_FOUND);
        }
    }
}
