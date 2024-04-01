package com.keshavarzi.airplanestats.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "user", schema = "user")
public class UserEntity {

    @Id
    @Column(name = "email", table = "user", unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // Exclusion is because associations impact performance
    @OneToMany(mappedBy = "userEntity", targetEntity = Trip.class, orphanRemoval = true)
    @ToString.Exclude
    private List<Trip> savedRoutes;

    @OneToOne(mappedBy = "email", targetEntity = RoleEntity.class, orphanRemoval = true)
    private RoleEntity roleEntity;
}
