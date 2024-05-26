package ru.vsu.restobook_backend.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.stereotype.Service;
import ru.vsu.restobook_backend.dto.RestaurantDto;
import ru.vsu.restobook_backend.model.Restaurant;
import ru.vsu.restobook_backend.repository.RestaurantsRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Log4j2
public class RestaurantsService {

    private final RestaurantsRepository restaurantsRepository;

    public void createRestaurant(RestaurantDto restaurantDto) {
        List<String> validationErrors = new ArrayList<>();
        boolean legalEntityNameUnique = restaurantsRepository.findByLegalEntityName(restaurantDto.legalEntityName()).isEmpty();
        if (!legalEntityNameUnique) {
            validationErrors.add("Legal entity name must be unique");
        }
        boolean innUnique = restaurantsRepository.findByInn(restaurantDto.inn()).isEmpty();
        if (!innUnique) {
            validationErrors.add("INN must be unique");
        }

        if (!validationErrors.isEmpty()) {
            log.log(Level.INFO, "Can't create restaurant");
            throw new ValidationError(validationErrors);
        }

        Restaurant restaurant = new Restaurant();
        restaurant.setName(restaurantDto.name());
        restaurant.setLegalEntityName(restaurantDto.legalEntityName());
        restaurant.setInn(restaurantDto.inn());
        if (restaurantDto.comment().isPresent()) {
            restaurant.setComment(restaurantDto.comment().get());
        }

        restaurantsRepository.save(restaurant);
    }

    public List<Restaurant> getAll() {
        return restaurantsRepository.findAll();
    }
}
