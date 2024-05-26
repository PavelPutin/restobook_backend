package ru.vsu.restobook_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.restobook_backend.model.Reservation;
import ru.vsu.restobook_backend.model.Restaurant;

import java.util.List;

@Repository
public interface ReservationsRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findAllByRestaurant(Restaurant restaurant);
}
