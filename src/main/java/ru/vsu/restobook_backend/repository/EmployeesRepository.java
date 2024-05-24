package ru.vsu.restobook_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.restobook_backend.model.Employee;

@Repository
public interface EmployeesRepository extends JpaRepository<Employee, Integer> {
}
