package ru.vsu.restobook_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.restobook_backend.model.Restaurant;

@Repository
public interface RestaurantsRepository extends JpaRepository<Restaurant, Integer> {
}
