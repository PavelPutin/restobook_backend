package ru.vsu.restobook_backend.model;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

//import javax.persistence.Entity;
import java.util.List;

@Entity(name = "restaurant")
@Data
@NoArgsConstructor
public class Restaurant {
    private int id;
    private String name;
    private String legalEntityName;
    private String inn;
    private String comment;
    private List<Employee> employees;
    private List<Table> tables;
    private List<Reservation> reservations;

    // Геттеры и сеттеры для всех полей

}