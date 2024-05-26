package ru.vsu.restobook_backend.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.vsu.restobook_backend.dto.TableDto;
import ru.vsu.restobook_backend.model.Table;
import ru.vsu.restobook_backend.repository.TablesRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.Collections.emptyList;

@Service
@AllArgsConstructor
@Log4j2
public class TablesService {
    private final TablesRepository tablesRepository;
    private final SecurityService securityService;
    private final RestaurantsService restaurantsService;

    public Table createTable(int restaurantId, TableDto tableDto, JwtAuthenticationToken principal) {
        var restaurant = restaurantsService.getById(restaurantId);

        if (!securityService.isRestaurantAdmin(restaurantId, principal)) {
            throw new RestaurantForbiddenException(singletonList("You are not the admin of restaurant " + restaurantId));
        }

        List<String> validationErrors = new ArrayList<>();

        boolean tableNumberIsPositive = tableDto.number() > 0;
        if (!tableNumberIsPositive) {
            validationErrors.add("Table number must be greater then zero");
        }

        boolean tableNumberUniqueInRestaurant =
                tablesRepository.findByTableNumberAndRestaurant(tableDto.number(), restaurant)
                        .isEmpty();
        if (!tableNumberUniqueInRestaurant) {
            validationErrors.add("Table number must be unique in restaurant " + restaurantId);
        }

        boolean seatsNumberIsPositive = tableDto.seatsNumber() > 0;
        if (!seatsNumberIsPositive) {
            validationErrors.add("Table seats number must be greater then zero");
        }

        if (!validationErrors.isEmpty()) {
            log.log(Level.INFO, "Can't create table");
            throw new ValidationError(validationErrors);
        }

        Table table = new Table();
        table.setTableNumber(tableDto.number());
        table.setSeatsNumber(tableDto.seatsNumber());
        tableDto.state().ifPresent(table::setState);
        tableDto.comment().ifPresent(table::setComment);
        table.setRestaurant(restaurant);
        table.setReservations(emptyList());

        return tablesRepository.save(table);
    }

    public List<Table> getAll(int restaurantId, JwtAuthenticationToken principal) {
        var restaurant = restaurantsService.getById(restaurantId);

        if (!securityService.isRestaurantUser(restaurantId, principal) &&
                !securityService.isRestaurantAdmin(restaurantId, principal)) {
            throw new RestaurantForbiddenException(singletonList("You are not the employee of restaurant " + restaurantId));
        }

        return tablesRepository.findAllByRestaurant(restaurant);
    }

    public Table getTableById(int restaurantId, int tableId, JwtAuthenticationToken principal) {
        restaurantsService.getById(restaurantId);

        if (!securityService.isRestaurantUser(restaurantId, principal) &&
                !securityService.isRestaurantAdmin(restaurantId, principal)) {
            throw new RestaurantForbiddenException(singletonList("You are not the employee of restaurant " + restaurantId));
        }

        var tableOpt = tablesRepository.findById(tableId);
        return tableOpt.orElseThrow(() -> new NotFoundException(List.of("Table not found with id " + restaurantId)));
    }

    public Table updateTable(int restaurantId, int tableId, TableDto tableDto, JwtAuthenticationToken principal) {
        var table = getTableById(restaurantId, tableId, principal);

        List<String> validationErrors = new ArrayList<>();

        boolean tableNumberIsPositive = tableDto.number() > 0;
        if (!tableNumberIsPositive) {
            validationErrors.add("Table number must be greater then zero");
        }

        boolean tableNumberUniqueInRestaurant =
                table.getTableNumber() == tableDto.number() ||
                        tablesRepository.findByTableNumberAndRestaurant(tableDto.number(), table.getRestaurant())
                        .isEmpty();
        if (!tableNumberUniqueInRestaurant) {
            validationErrors.add("Table number must be unique in restaurant " + restaurantId);
        }

        boolean seatsNumberIsPositive = tableDto.seatsNumber() > 0;
        if (!seatsNumberIsPositive) {
            validationErrors.add("Table seats number must be greater then zero");
        }

        if (!validationErrors.isEmpty()) {
            log.log(Level.INFO, "Can't create table");
            throw new ValidationError(validationErrors);
        }

        table.setTableNumber(tableDto.number());
        table.setSeatsNumber(tableDto.seatsNumber());
        tableDto.state().ifPresent(table::setState);
        tableDto.comment().ifPresent(table::setComment);

        return tablesRepository.save(table);
    }
}
