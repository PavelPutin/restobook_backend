package ru.vsu.restobook_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

//import javax.persistence.Entity;
import java.util.List;

@Entity(name = "restaurant")
@Data
@NoArgsConstructor
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "rest_name")
    private String name;
    @Column(name = "legal_entity_name")
    private String legalEntityName;
    private String inn;
    @Column(name = "rest_comment")
    private String comment;
    @OneToMany(mappedBy = "restaurant")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Employee> employees;
    @OneToMany(mappedBy = "restaurant")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Table> tables;
    @OneToMany(mappedBy = "restaurant")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Reservation> reservations;

    // Геттеры и сеттеры для всех полей

}