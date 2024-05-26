package ru.vsu.restobook_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity(name = "table_data")
@Data
@NoArgsConstructor
public class Table {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id ;
    @Column(name = "table_number")
    private int tableNumber;
    @Column(name = "seats_number")
    private int seatsNumber;
    @Enumerated(EnumType.STRING)
    private TableState state;
    @Column(name = "table_comment")
    private String comment;
    @ManyToOne
    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Restaurant restaurant;
    @ManyToMany
    @JoinTable(
            name = "table_reservation",
            joinColumns = @JoinColumn(name = "table_id"),
            inverseJoinColumns = @JoinColumn(name = "reservation_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Reservation> reservations;
}
