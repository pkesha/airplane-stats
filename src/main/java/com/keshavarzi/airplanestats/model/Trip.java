package com.keshavarzi.airplanestats.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "trip")
public class Trip {

    @Id
    @Column(name = "trip_id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email", nullable = false, table = "trip")
    private UserEntity userEntity;

    @OneToMany(mappedBy = "trip", targetEntity = Route.class)
    private List<Route> routes;

    @Column(name = "trip_name", table = "trip")
    private String tripName;

    @Column(name = "trip_start", table = "trip")
    private ZonedDateTime tripStart;

    @Column(name = "trip_end", table = "trip")
    private ZonedDateTime tripEnd;
}
