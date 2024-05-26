package ru.vsu.restobook_backend.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.vsu.restobook_backend.dto.ErrorDto;
import ru.vsu.restobook_backend.dto.TableDto;
import ru.vsu.restobook_backend.mapper.TableMapper;
import ru.vsu.restobook_backend.model.Table;
import ru.vsu.restobook_backend.service.NotFoundException;
import ru.vsu.restobook_backend.service.RestaurantForbiddenException;
import ru.vsu.restobook_backend.service.TablesService;
import ru.vsu.restobook_backend.service.ValidationError;

import java.time.Instant;
import java.util.List;

@Controller
@RequestMapping("/restaurant/{restaurantId}/table")
@AllArgsConstructor
public class TablesController {

    private final TablesService tablesService;
    private final TableMapper tableMapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('restobook_admin')")
    public ResponseEntity<?> createTable(
            @PathVariable int restaurantId,
            @RequestBody TableDto tableDto,
            JwtAuthenticationToken principal) {
        try {
            var table = tablesService.createTable(restaurantId, tableDto, principal);
            var result = tableMapper.toDto(table);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (ValidationError e) {
            return ResponseEntity.badRequest().body(new ErrorDto(Instant.now(), e.getErrors()));
        } catch (NotFoundException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.NOT_FOUND);
        } catch (RestaurantForbiddenException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('restobook_admin', 'restobook_user')")
    public ResponseEntity<?> getAllTablesByRestaurant(@PathVariable int restaurantId, JwtAuthenticationToken principal) {
        try {
            List<Table> table = tablesService.getAll(restaurantId, principal);
            var result = table.stream().map(tableMapper::toDto).toList();
            return ResponseEntity.ok(result);
        }catch (NotFoundException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.NOT_FOUND);
        } catch (RestaurantForbiddenException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/{tableId}")
    @PreAuthorize("hasAnyRole('restobook_admin', 'restobook_user')")
    public ResponseEntity<?> getAllTablesByRestaurant(
            @PathVariable int restaurantId,
            @PathVariable int tableId,
            JwtAuthenticationToken principal) {
        try {
            Table table = tablesService.getTableById(restaurantId, tableId, principal);
            var result = tableMapper.toDto(table);
            return ResponseEntity.ok(result);
        }catch (NotFoundException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.NOT_FOUND);
        } catch (RestaurantForbiddenException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/{tableId}")
    @PreAuthorize("hasAnyRole('restobook_admin', 'restobook_user')")
    public ResponseEntity<?> updateTable(@PathVariable int restaurantId,
                                         @PathVariable int tableId,
                                         @RequestBody TableDto tableDto,
                                         JwtAuthenticationToken principal) {
        try {
            var table = tablesService.updateTable(restaurantId, tableId, tableDto, principal);
            var result = tableMapper.toDto(table);
            return ResponseEntity.ok(result);
        } catch (ValidationError e) {
            return ResponseEntity.badRequest().body(new ErrorDto(Instant.now(), e.getErrors()));
        } catch (NotFoundException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.NOT_FOUND);
        } catch (RestaurantForbiddenException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.FORBIDDEN);
        }
    }
}
