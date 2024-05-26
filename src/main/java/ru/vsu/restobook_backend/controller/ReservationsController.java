package ru.vsu.restobook_backend.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.vsu.restobook_backend.dto.ErrorDto;
import ru.vsu.restobook_backend.dto.ReservationDto;
import ru.vsu.restobook_backend.dto.TableDto;
import ru.vsu.restobook_backend.mapper.ReservationMapper;
import ru.vsu.restobook_backend.model.Reservation;
import ru.vsu.restobook_backend.model.Table;
import ru.vsu.restobook_backend.service.NotFoundException;
import ru.vsu.restobook_backend.service.ReservationsService;
import ru.vsu.restobook_backend.service.RestaurantForbiddenException;
import ru.vsu.restobook_backend.service.ValidationError;

import java.time.Instant;
import java.util.List;

@Controller
@RequestMapping("/restaurant/{restaurantId}/reservation")
@AllArgsConstructor
public class ReservationsController {

    private final ReservationsService reservationsService;
    private final ReservationMapper reservationMapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('restobook_admin', 'restobook_user')")
    public ResponseEntity<?> createReservation(
            @PathVariable int restaurantId,
            @RequestBody ReservationDto reservationDto,
            JwtAuthenticationToken principal) {
        try {
            Reservation reservation = reservationsService.createReservation(restaurantId, reservationDto, principal);
            var result = reservationMapper.toDto(reservation);
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
    public ResponseEntity<?> getAllReservationByRestaurant(
            @PathVariable int restaurantId,
            JwtAuthenticationToken principal) {
        try {
            List<Reservation> reservation = reservationsService.getAll(restaurantId, principal);
            var result = reservation.stream().map(reservationMapper::toDto).toList();
            return ResponseEntity.ok(result);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.NOT_FOUND);
        } catch (RestaurantForbiddenException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/{reservationId}")
    @PreAuthorize("hasAnyRole('restobook_admin', 'restobook_user')")
    public ResponseEntity<?> getReservationById(
            @PathVariable int restaurantId,
            @PathVariable int reservationId,
            JwtAuthenticationToken principal) {
        try {
            Reservation reservation = reservationsService.getReservationById(restaurantId, reservationId, principal);
            var result = reservationMapper.toDto(reservation);
            return ResponseEntity.ok(result);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.NOT_FOUND);
        } catch (RestaurantForbiddenException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/{reservationId}")
    @PreAuthorize("hasAnyRole('restobook_admin', 'restobook_user')")
    public ResponseEntity<?> updateReservation(@PathVariable int restaurantId,
                                         @PathVariable int reservationId,
                                         @RequestBody ReservationDto reservationDto,
                                         JwtAuthenticationToken principal) {
        try {
            var reservation = reservationsService.updateReservation(restaurantId, reservationId, reservationDto, principal);
            var result = reservationMapper.toDto(reservation);
            return ResponseEntity.ok(result);
        } catch (ValidationError e) {
            return ResponseEntity.badRequest().body(new ErrorDto(Instant.now(), e.getErrors()));
        } catch (NotFoundException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.NOT_FOUND);
        } catch (RestaurantForbiddenException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{reservationId}")
    @PreAuthorize("hasAnyRole('restobook_admin', 'restobook_user')")
    public ResponseEntity<?> deleteTable(@PathVariable int restaurantId,
                                         @PathVariable int reservationId,
                                         JwtAuthenticationToken principal) {
        try {
            reservationsService.deleteReservation(restaurantId, reservationId, principal);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.NOT_FOUND);
        } catch (RestaurantForbiddenException e) {
            return new ResponseEntity<>(new ErrorDto(Instant.now(), e.getErrors()), HttpStatus.FORBIDDEN);
        }
    }
}
