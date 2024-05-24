package ru.vsu.restobook_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "employee")
@Data
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String login;
    private String surname;
    @Column(name = "employee_name")
    private String name;
    private String patronymic;
    @Column(name = "employee_comment")
    private String comment;
    @Column(name = "changed_pass")
    private boolean changedPass;
    @ManyToOne
    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    private Restaurant restaurant;
}
