package ru.vsu.restobook_backend.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.vsu.restobook_backend.dto.ReservationDto;
import ru.vsu.restobook_backend.model.Reservation;
import ru.vsu.restobook_backend.model.ReservationState;
import ru.vsu.restobook_backend.model.Table;
import ru.vsu.restobook_backend.repository.ReservationsRepository;
import ru.vsu.restobook_backend.repository.TablesRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Service
@AllArgsConstructor
@Log4j2
public class ReservationsService {

    private final RestaurantsService restaurantsService;
    private final SecurityService securityService;
    private final ReservationsRepository reservationsRepository;
    private final TablesRepository tablesRepository;

    @Transactional
    public Reservation createReservation(int restaurantId, ReservationDto reservationDto, JwtAuthenticationToken principal) {
        var restaurant = restaurantsService.getById(restaurantId);

        if (!securityService.isRestaurantUser(restaurantId, principal) &&
                !securityService.isRestaurantAdmin(restaurantId, principal)) {
            throw new RestaurantForbiddenException(singletonList("You are not the employee of restaurant " + restaurantId));
        }

        List<String> validationErrors = new ArrayList<>();

        boolean clientPhoneNumberLengthLE30 = reservationDto.clientPhoneNumber().length() <= 30;
        if (!clientPhoneNumberLengthLE30) {
            validationErrors.add("Client phone number length must be less or equal to 30 characters");
        }

        boolean clientNameLengthLE512 = reservationDto.clientName().length() <= 512;
        if (!clientNameLengthLE512) {
            validationErrors.add("Client name length must be less or equal to 512 characters");
        }

        boolean durationIntervalPositive = reservationDto.durationIntervalMinutes() > 0;
        if (!durationIntervalPositive) {
            validationErrors.add("Duration interval must be positive");
        }

        if (!validationErrors.isEmpty()) {
            log.log(Level.INFO, "Can't create table");
            throw new ValidationError(validationErrors);
        }

        var reservation = new Reservation();
        reservation.setPersonsNumber(reservationDto.personsNumber());
        reservation.setClientPhoneNumber(reservationDto.clientPhoneNumber());
        reservation.setClientName(reservationDto.clientName());
        reservation.setStartDateTime(reservationDto.startDateTime());
        reservation.setDuration(Duration.ofMinutes(reservationDto.durationIntervalMinutes()));
        reservation.setEmployeeFullName(reservationDto.employeeFullName());
        reservation.setState(reservationDto.state().orElse(ReservationState.WAITING));
        reservation.setCreatingDateTime(Instant.now());
        reservationDto.comment().ifPresent(reservation::setReservationComment);
        reservation.setRestaurant(restaurant);

        List<Integer> tableIds = reservationDto.tableIds().orElse(emptyList());
        List<Table> tables = tablesRepository.findAllByIdIn(tableIds);
        Set<Integer> actualId = tables.stream().map(Table::getId).collect(Collectors.toSet());

        List<String> notFoundMessages = new ArrayList<>();
        for (int id : tableIds) {
            if (!actualId.contains(id)) {
                notFoundMessages.add("Not found table with id " + id);
            }
        }

        if (!notFoundMessages.isEmpty()) {
            throw new NotFoundException(notFoundMessages);
        }

        reservation.setTables(tables);

        for (var table : tables) {
            table.getReservations().add(reservation);
        }
        return reservationsRepository.save(reservation);
    }


    public List<Reservation> getAll(int restaurantId, JwtAuthenticationToken principal) {
        var restaurant = restaurantsService.getById(restaurantId);

        if (!securityService.isRestaurantUser(restaurantId, principal) &&
                !securityService.isRestaurantAdmin(restaurantId, principal)) {
            throw new RestaurantForbiddenException(singletonList("You are not the employee of restaurant " + restaurantId));
        }

        return reservationsRepository.findAllByRestaurant(restaurant);
    }
}
