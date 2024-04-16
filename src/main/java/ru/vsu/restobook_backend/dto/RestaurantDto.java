package ru.vsu.restobook_backend.dto;

import java.util.Optional;

public record RestaurantDto(
        Optional<Integer> id,
        String name,
        String legalEntityName,
        String inn,
        Optional<String> comment
) {
}
