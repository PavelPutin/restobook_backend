package ru.vsu.restobook_backend.model;

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
