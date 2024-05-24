package ru.vsu.restobook_backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.time.Duration;
import java.util.List;

@Entity(name = "reservation")
@Data
@NoArgsConstructor
public class Reservation {
    private int id;
    private int personsNumber;
    private String clientPhoneNumber;
    private String clientName;
    private Instant startDateTime;
    private Duration duration;
    private String employeeFullName;
    private Instant creatingDateTime;
    private String state; // Необходимо уточнить название поля
    private String reservationComment;
    @ManyToOne
    @JoinColumn(name = "restaurant_ID")
    private Restaurant restaurant;
    private List<Table> tables;


}