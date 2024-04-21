package ru.vsu.restobook_backend.mapper;

import ru.vsu.restobook_backend.dto.RestaurantDto;
import ru.vsu.restobook_backend.model.Restaurant;

import java.util.Optional;

public class RestaurantMapper {

    public RestaurantDto toDto(Restaurant restaurant) {
        return new RestaurantDto(
                Optional.ofNullable(restaurant.getId()),
                restaurant.getName(),
                restaurant.getLegalEntityName(),
                restaurant.getInn(),
                Optional.ofNullable(restaurant.getComment())
        );
    }
}