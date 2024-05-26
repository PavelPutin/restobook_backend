package ru.vsu.restobook_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.restobook_backend.model.Employee;
import ru.vsu.restobook_backend.model.Restaurant;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeesRepository extends JpaRepository<Employee, Integer> {
    Optional<Employee> findByLogin(String login);
    List<Employee> findAllByRestaurant(Restaurant restaurant);
}
