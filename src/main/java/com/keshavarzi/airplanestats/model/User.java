package com.keshavarzi.airplanestats.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter @Setter @ToString
@Entity @Table(name="users")
public class User {

    @Id
    @Column(name = "email", unique = true, nullable = false, table = "users")
    private String email;

    @Column
    private String password;

    // Exclusion is because associations impact performance
    @OneToMany(mappedBy = "user", targetEntity = Trip.class)
    @ToString.Exclude
    private List<Trip> savedRoutes;
}
