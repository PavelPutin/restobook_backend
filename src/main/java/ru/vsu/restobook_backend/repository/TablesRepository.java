package ru.vsu.restobook_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.restobook_backend.model.Restaurant;
import ru.vsu.restobook_backend.model.Table;

import java.util.List;

@Repository
public interface TablesRepository extends JpaRepository<Table, Integer> {
    List<Table> findByTableNumberAndRestaurant(int tableNumber, Restaurant restaurant);
    List<Table> findAllByRestaurant(Restaurant restaurant);
}
