package com.keshavarzi.airplanestats.model;

import jakarta.annotation.Nonnull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collection;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user", schema = "user_data")
public class UserEntity {

    @Id
    @Nonnull
    @Column(name = "email", table = "user", unique = true, nullable = false)
    private String email;

    @Nonnull
    @Column(name = "password", table = "user", nullable = false)
    private String password;

    // Exclusion is because associations impact performance
    @OneToMany(mappedBy = "userEntity", targetEntity = Trip.class, orphanRemoval = true)
    @ToString.Exclude
    private Collection<Trip> savedRoutes;

    // Do not need to do it on the other entity
    @ManyToMany(fetch = FetchType.EAGER, targetEntity = RoleEntity.class, cascade = CascadeType.ALL)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_email", table = "role", referencedColumnName = "email", nullable = false,
            foreignKey = @ForeignKey(name = "roles_users_email_fk")),
    inverseJoinColumns = @JoinColumn(name = "role_id", table = "role", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "user_role_role_id_fk")))
    private Collection<RoleEntity> roleEntities;
}
