package ru.vsu.restobook_backend.mapper;

import ru.vsu.restobook_backend.dto.ReservationDto;
import ru.vsu.restobook_backend.model.Reservation;

import java.util.Optional;
import java.util.stream.Collectors;

public class ReservationMapper {

    public static ReservationDto toDto(Reservation reservation) {
        return new ReservationDto(
                reservation.getId(),
                reservation.getPersonsNumber(),
                reservation.getClientPhoneNumber(),
                reservation.getClientName(),
                reservation.getStartDateTime(),
                reservation.getDuration(),
                reservation.getEmployeeFullName(),
                reservation.getCreatingDateTime(),
                Optional.ofNullable(reservation.getState()),
                Optional.ofNullable(reservation.getReservationComment()),
                Optional.ofNullable(reservation.getRestaurant() != null ? reservation.getRestaurant().getId() : null),
                Optional.ofNullable(reservation.getTables())
        );
    }
}