package com.keshavarzi.airplanestats.model;

import jakarta.annotation.Nonnull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "route", schema = "user_data")
public class RouteEntity {

    @Id
    @Nonnull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", table = "route", unique = true, nullable = false)
    private Long routeId;

    // Returning a reference to a mutable object value stored in one of the object's fields exposes the internal representation of the object.
    // If instances are accessed by untrusted code, and unchecked changes to the mutable object would compromise security or other important properties,
    @Nonnull
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ManyToOne(targetEntity = UserEntity.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_email", referencedColumnName = "email", table = "route", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "route_user_email_fk"))
    private UserEntity userEntity;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ManyToOne(targetEntity = Trip.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "trip_id", referencedColumnName = "id", table = "route",
            foreignKey = @ForeignKey(name = "route_trip_trip_id_fk"))
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
