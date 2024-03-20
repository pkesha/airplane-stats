package com.keshavarzi.airplanestats.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="users")
public class User {

    @Data
    @Column(unique = true)
    private String emailAddress;

    @Data
    @Column
    private String password;

}
