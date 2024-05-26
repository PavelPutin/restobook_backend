package ru.vsu.restobook_backend.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.vsu.restobook_backend.dto.EmployeeDto;
import ru.vsu.restobook_backend.dto.ErrorDto;
import ru.vsu.restobook_backend.service.AccountsService;
import ru.vsu.restobook_backend.service.NotFoundException;
import ru.vsu.restobook_backend.service.ValidationError;

import java.time.Instant;

@Controller
@RequestMapping("/restaurant/{restaurantId}/employee")
@AllArgsConstructor
public class AccountsController {
    private final AccountsService accountsService;

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
}
