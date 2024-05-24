package ru.vsu.restobook_backend.dto;

import ru.vsu.restobook_backend.model.ReservationState;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public record ReservationDto(
        Optional<Integer> id,
        int personsNumber,
        String clientPhoneNumber,
        String clientName,
        Instant startDateTime,
        long durationIntervalMinutes,
        String employeeFullName,
        Instant creatingDateTime,
        Optional<ReservationState> state,
        Optional<String> comment,
        Optional<Integer> restaurantId,
        Optional<List<Integer>>tableIds
) {
}
