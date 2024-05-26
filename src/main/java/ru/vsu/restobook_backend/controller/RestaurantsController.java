package ru.vsu.restobook_backend.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.vsu.restobook_backend.dto.ErrorDto;
import ru.vsu.restobook_backend.dto.RestaurantDto;
import ru.vsu.restobook_backend.mapper.RestaurantMapper;
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
    private final RestaurantMapper restaurantMapper;

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
    public ResponseEntity<List<RestaurantDto>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantService.getAll();
        List<RestaurantDto> result = restaurants.stream().map(restaurantMapper::toDto).toList();
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

    @PutMapping("/{restaurantId}")
    @PreAuthorize("hasAnyRole('vendor_admin')")
    public ResponseEntity<?> updateRestaurant(@PathVariable int restaurantId, @RequestBody RestaurantDto restaurantDto) {
        try {
            restaurantService.update(restaurantId, restaurantDto);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.NOT_FOUND);
        } catch (ValidationError e) {
            return ResponseEntity.badRequest().body(new ErrorDto(Instant.now(), e.getErrors()));
        }
    }

    @DeleteMapping("/{restaurantId}")
    @PreAuthorize("hasAnyRole('vendor_admin')")
    public ResponseEntity<?> deleteRestaurant(@PathVariable int restaurantId) {
        try {
            restaurantService.delete(restaurantId);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.NOT_FOUND);
        }
    }
}
