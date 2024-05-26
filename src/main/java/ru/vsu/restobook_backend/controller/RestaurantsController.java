package ru.vsu.restobook_backend.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.vsu.restobook_backend.dto.ErrorDto;
import ru.vsu.restobook_backend.dto.RestaurantDto;
import ru.vsu.restobook_backend.service.RestaurantsService;
import ru.vsu.restobook_backend.service.ValidationError;

import java.time.Instant;

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
}
