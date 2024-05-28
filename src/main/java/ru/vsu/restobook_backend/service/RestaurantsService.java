package ru.vsu.restobook_backend.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.stereotype.Service;
import ru.vsu.restobook_backend.dto.RestaurantDto;
import ru.vsu.restobook_backend.model.Restaurant;
import ru.vsu.restobook_backend.repository.AccountsRepository;
import ru.vsu.restobook_backend.repository.RestaurantsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Log4j2
public class RestaurantsService {

    private final RestaurantsRepository restaurantsRepository;
    private final KeycloakService keycloakService;

    public Restaurant createRestaurant(RestaurantDto restaurantDto) {
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

        return restaurantsRepository.save(restaurant);
    }

    public List<Restaurant> getAll() {
        return restaurantsRepository.findAll();
    }

    public Restaurant getById(int restaurantId) {
        Optional<Restaurant> restaurant = restaurantsRepository.findById(restaurantId);
        return restaurant.orElseThrow(() -> new NotFoundException(List.of("Restaurant not found with id " + restaurantId)));
    }

    public Restaurant update(int restaurantId, RestaurantDto restaurantDto) {
        Optional<Restaurant> restaurantOptional = restaurantsRepository.findById(restaurantId);
        Restaurant restaurant = restaurantOptional.orElseThrow(() -> new NotFoundException(List.of("Restaurant not found with id " + restaurantId)));

        List<String> validationErrors = new ArrayList<>();
        Optional<Restaurant> byLegalName = restaurantsRepository.findByLegalEntityName(restaurantDto.legalEntityName());
        boolean legalEntityNameUnique = byLegalName.isEmpty() || byLegalName.get().getId() == restaurantId;
        if (!legalEntityNameUnique) {
            validationErrors.add("Legal entity name must be unique");
        }

        Optional<Restaurant> byInn = restaurantsRepository.findByInn(restaurantDto.inn());
        boolean innUnique = byInn.isEmpty() || byInn.get().getId() == restaurantId;
        if (!innUnique) {
            validationErrors.add("INN must be unique");
        }

        if (!validationErrors.isEmpty()) {
            log.log(Level.INFO, "Can't update restaurant");
            throw new ValidationError(validationErrors);
        }

        restaurant.setId(restaurantId);
        restaurant.setName(restaurantDto.name());
        restaurant.setLegalEntityName(restaurantDto.legalEntityName());
        restaurant.setInn(restaurantDto.inn());
        if (restaurantDto.comment().isPresent()) {
            restaurant.setComment(restaurantDto.comment().get());
        }

        return restaurantsRepository.save(restaurant);
    }

    public void delete(int restaurantId) {
        Optional<Restaurant> restaurantOptional = restaurantsRepository.findById(restaurantId);
        if (restaurantOptional.isEmpty()) {
            throw new NotFoundException(List.of("Restaurant not found with id " + restaurantId));
        }
        Restaurant restaurant = restaurantOptional.get();
        for (var employee : restaurant.getEmployees()) {
            try {
                keycloakService.deleteEmployee(employee.getLogin());
            } catch (RuntimeException e) {
                log.log(Level.ERROR, "Can't delete user " + employee.getId());
            }
        }
        restaurantsRepository.deleteById(restaurantId);
    }
}
