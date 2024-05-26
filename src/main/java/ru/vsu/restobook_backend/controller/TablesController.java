package ru.vsu.restobook_backend.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.vsu.restobook_backend.dto.ErrorDto;
import ru.vsu.restobook_backend.dto.TableDto;
import ru.vsu.restobook_backend.mapper.TableMapper;
import ru.vsu.restobook_backend.service.NotFoundException;
import ru.vsu.restobook_backend.service.RestaurantForbiddenException;
import ru.vsu.restobook_backend.service.TablesService;
import ru.vsu.restobook_backend.service.ValidationError;

import java.time.Instant;

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
}
