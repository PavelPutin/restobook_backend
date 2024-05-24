package ru.vsu.restobook_backend.model;

import io.hypersistence.utils.hibernate.type.interval.PostgreSQLIntervalType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.time.Duration;
import java.util.List;

@Entity(name = "reservation")
@Data
@NoArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "persons_number")
    private int personsNumber;
    @Column(name = "client_phone_number")
    private String clientPhoneNumber;
    @Column(name = "client_name")
    private String clientName;
    @Column(name = "start_date_time")
    private Instant startDateTime;
    @Column(name = "duration")
    @Type(PostgreSQLIntervalType.class)
    private Duration duration;
    @Column(name = "employee_full_name")
    private String employeeFullName;
    @Column(name = "creating_date_time")
    private Instant creatingDateTime;
    @Enumerated(EnumType.STRING)
    private ReservationState state; // Необходимо уточнить название поля
    @Column(name = "reservation_comment")
    private String reservationComment;
    @ManyToOne
    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    private Restaurant restaurant;
    @ManyToMany
    private List<Table> tables;


}