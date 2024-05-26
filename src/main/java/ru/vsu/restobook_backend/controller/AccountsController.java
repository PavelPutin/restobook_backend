package ru.vsu.restobook_backend.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.vsu.restobook_backend.dto.EmployeeDto;
import ru.vsu.restobook_backend.dto.ErrorDto;
import ru.vsu.restobook_backend.mapper.EmployeeMapper;
import ru.vsu.restobook_backend.model.Employee;
import ru.vsu.restobook_backend.service.AccountsService;
import ru.vsu.restobook_backend.service.NotFoundException;
import ru.vsu.restobook_backend.service.RestaurantForbiddenException;
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
    public ResponseEntity<?> createEmployee(@PathVariable int restaurantId, @RequestBody EmployeeDto employeeDto, JwtAuthenticationToken principal) {
        try {
            accountsService.createEmployee(restaurantId, employeeDto, principal);
            return ResponseEntity.ok().build();
        } catch (ValidationError e) {
            return ResponseEntity.badRequest().body(new ErrorDto(Instant.now(), e.getErrors()));
        } catch (NotFoundException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.NOT_FOUND);
        } catch (RestaurantForbiddenException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('vendor_admin', 'restobook_admin')")
    public ResponseEntity<?> getAllEmployeesByRestaurantId(@PathVariable int restaurantId, JwtAuthenticationToken principal) {
        try {
            List<Employee> employees = accountsService.getEmployees(restaurantId, principal);
            List<EmployeeDto> result = employees.stream().map(employeeMapper::toDto).toList();
            return ResponseEntity.ok().body(result);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.NOT_FOUND);
        } catch (RestaurantForbiddenException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<?> getEmployeeById(@PathVariable int restaurantId, @PathVariable int employeeId, JwtAuthenticationToken principal) {
        try {
            Employee employee = accountsService.getEmployeeById(restaurantId, employeeId, principal);
            EmployeeDto result = employeeMapper.toDto(employee);
            return ResponseEntity.ok(result);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.NOT_FOUND);
        } catch (RestaurantForbiddenException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.FORBIDDEN);
        }
    }
}
