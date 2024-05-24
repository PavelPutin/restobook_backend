package ru.vsu.restobook_backend.mapper;

import org.springframework.stereotype.Service;
import ru.vsu.restobook_backend.dto.RestaurantDto;
import ru.vsu.restobook_backend.model.Restaurant;

import java.util.Optional;

@Service
public class RestaurantMapper {
    public RestaurantDto toDto(Restaurant restaurant) {
        return new RestaurantDto(
                Optional.of(restaurant.getId()),
                restaurant.getName(),
                restaurant.getLegalEntityName(),
                restaurant.getInn(),
                Optional.ofNullable(restaurant.getComment())
        );
    }
}
