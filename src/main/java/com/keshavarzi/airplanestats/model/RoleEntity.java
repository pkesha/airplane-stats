package com.keshavarzi.airplanestats.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "role", schema = "user")
public class RoleEntity {
    @Id
    @Column(name = "role_id", table = "role", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @JoinColumn(name = "user_email", referencedColumnName = "email", table = "role", nullable = false, unique = true)
    private String email;

    @Column(name = "user_role", table = "role", nullable = false)
    private String userRole;
}