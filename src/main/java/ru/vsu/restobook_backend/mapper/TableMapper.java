package ru.vsu.restobook_backend.mapper;

import org.springframework.stereotype.Service;
import ru.vsu.restobook_backend.dto.ReservationDto;
import ru.vsu.restobook_backend.dto.TableDto;
import ru.vsu.restobook_backend.model.Reservation;
import ru.vsu.restobook_backend.model.Table;

import java.util.Optional;

@Service
public class TableMapper {
    public TableDto toDto(Table table) {
        return new TableDto(
                Optional.of(table.getId()),
                table.getTableNumber(),
                table.getSeatsNumber(),
                Optional.of(table.getState()),
                Optional.of(table.getRestaurant().getId()),
                Optional.of(table.getReservations().stream().map(Reservation::getId).toList())
        );
    }
}
