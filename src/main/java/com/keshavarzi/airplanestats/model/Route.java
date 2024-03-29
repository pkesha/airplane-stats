package com.keshavarzi.airplanestats.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "route")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id", table = "route")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email", nullable = false, table = "users")
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "trip_id", referencedColumnName = "trip_id", table = "trip")
    private Trip trip;

    @Column(name = "flight_number", table = "route")
    private String flightNumber;

    @Column(name = "origin", table = "route")
    private String origin;

    @Column(name = "destination", table = "route")
    private String destination;

    @Column(name = "airplane_model", table = "route")
    private String airplaneModel;

    @Column(name = "airline", table = "route")
    private String airline;
}
