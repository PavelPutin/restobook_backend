package ru.vsu.restobook_backend.mapper;

import ru.vsu.restobook_backend.dto.ReservationDto;
import ru.vsu.restobook_backend.model.Reservation;
import ru.vsu.restobook_backend.model.Table;

import java.util.Optional;
import java.util.stream.Collectors;

public class ReservationMapper {

    public static ReservationDto toDto(Reservation reservation) {

        return new ReservationDto(
                Optional.of(reservation.getId()),
                reservation.getPersonsNumber(),
                reservation.getClientPhoneNumber(),
                reservation.getClientName(),
                reservation.getStartDateTime(),
                reservation.getDuration().toMinutes(),
                reservation.getEmployeeFullName(),
                reservation.getCreatingDateTime(),
                Optional.ofNullable(reservation.getState()),
                Optional.ofNullable(reservation.getReservationComment()),
                Optional.ofNullable(reservation.getRestaurant() != null ? reservation.getRestaurant().getId() : null),
                Optional.of(reservation.getTables().stream().map(Table::getId).toList())
        );
    }
}