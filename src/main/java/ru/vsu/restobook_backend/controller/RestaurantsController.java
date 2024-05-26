package ru.vsu.restobook_backend.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.vsu.restobook_backend.dto.ErrorDto;
import ru.vsu.restobook_backend.dto.RestaurantDto;
import ru.vsu.restobook_backend.model.Restaurant;
import ru.vsu.restobook_backend.service.NotFoundException;
import ru.vsu.restobook_backend.service.RestaurantsService;
import ru.vsu.restobook_backend.service.ValidationError;

import java.time.Instant;
import java.util.List;

@Controller

@AllArgsConstructor
@RequestMapping("/restaurant")
public class RestaurantsController {

    private final RestaurantsService restaurantService;

    @PostMapping
    @PreAuthorize("hasAnyRole('vendor_admin')")
    public ResponseEntity<?> createRestaurant(@RequestBody RestaurantDto restaurantDto) {
        try {
            restaurantService.createRestaurant(restaurantDto);
            return ResponseEntity.ok().build();
        } catch (ValidationError e) {
            return ResponseEntity.badRequest().body(new ErrorDto(Instant.now(), e.getErrors()));
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('vendor_admin')")
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        List<Restaurant> result = restaurantService.getAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{restaurantId}")
    @PreAuthorize("hasAnyRole('vendor_admin')")
    public ResponseEntity<?> getRestaurant(@PathVariable int restaurantId) {
        try {
            Restaurant result = restaurantService.getById(restaurantId);
            return ResponseEntity.ok(result);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.NOT_FOUND);
        }
    }
}
