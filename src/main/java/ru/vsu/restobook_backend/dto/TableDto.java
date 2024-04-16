package ru.vsu.restobook_backend.dto;

import ru.vsu.restobook_backend.model.TableState;

import java.util.List;
import java.util.Optional;

public record TableDto(
        Optional<Integer> id,
        int number,
        int seatsNumber,
        Optional<TableState> state,
        Optional<Integer> restaurantId,
        Optional<List<Integer>> reservationIds
) {
}
