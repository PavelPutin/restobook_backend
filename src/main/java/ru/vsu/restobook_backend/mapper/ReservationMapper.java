package ru.vsu.restobook_backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.vsu.restobook_backend.dto.ReservationDto;
import ru.vsu.restobook_backend.model.Reservation;

public interface ReservationMapper {

    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "personsNumber", source = "personsNumber")
    @Mapping(target = "clientPhoneNumber", source = "clientPhoneNumber")
    @Mapping(target = "clientName", source = "clientName")
    @Mapping(target = "startDateTime", source = "startDateTime")
    @Mapping(target = "durationIntervalMinutes", source = "durationIntervalMinutes")
    @Mapping(target = "employeeFullName", expression = "java(reservation.getEmployee().getFullName())")
    @Mapping(target = "creatingDateTime", source = "creatingDateTime")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "comment", source = "comment")
    @Mapping(target = "restaurantId", source = "restaurant.id")
    @Mapping(target = "tableIds", source = "tablesIds")
    ReservationDto reservationToReservationDto(Reservation reservation);
}