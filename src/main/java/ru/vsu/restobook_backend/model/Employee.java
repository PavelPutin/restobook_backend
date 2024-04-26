package ru.vsu.restobook_backend.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "Employee")
@Data
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String login;
    private String surname;
    private String name;
    private String patronymic;
    private String comment;
    private boolean changedPass;

    // Constructor, getters, setters, and other methods can be added here

}
