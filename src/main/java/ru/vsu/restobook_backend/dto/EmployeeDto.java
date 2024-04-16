package ru.vsu.restobook_backend.dto;

import java.util.Optional;

public record EmployeeDto(
        Optional<Integer> id,
        String login,
        String surname,
        String name,
        String patronymic,
        Optional<String> comment,
        Optional<Boolean> changedPassword,
        Optional<Integer> restaurantId
) {
}
