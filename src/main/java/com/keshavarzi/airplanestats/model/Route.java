package com.keshavarzi.airplanestats.model;

import jakarta.persistence.CascadeType;
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
@Table(name = "route", schema = "user")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id", table = "route", unique = true, nullable = false)
    private Long routeId;

    @ManyToOne(targetEntity = UserEntity.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_email", referencedColumnName = "email", table = "user", nullable = false, unique = true)
    private UserEntity userEntity;

    @ManyToOne(targetEntity = Trip.class)
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
