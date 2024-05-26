package ru.vsu.restobook_backend.dto;

import java.util.Optional;

public record EmployeeDto(
        Optional<Integer> id,
        String login,
        String surname,
        String name,
        Optional<String> password,
        Optional<String> role,
        Optional<String> patronymic,
        Optional<String> comment,
        Optional<Boolean> changedPassword,
        Optional<Integer> restaurantId
) {
}
