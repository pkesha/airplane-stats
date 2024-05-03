package com.keshavarzi.airplanestats.model;

import jakarta.annotation.Nonnull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Creates an object based plane-stats.user_data.user table. */
@Getter
@Setter
@NoArgsConstructor(force = true)
@Entity
@Table(name = "user", schema = "user_data")
public final class UserEntity {
  /**
   * Constructor for saving to database {@code plane-stats.user_data.user}.
   *
   * @param username User's email
   * @param password Password for security
   * @param roleEntities List of Spring Security Roles granted to user
   */
  public UserEntity(@Nonnull final String username,
                    @Nonnull final String password,
                    @Nonnull final Collection<RoleEntity> roleEntities) {
    this.username = username;
    this.password = password;
    this.roleEntities = List.copyOf(roleEntities);
    this.userId = this.getUserId();
    this.savedRoutes = null;
    this.savedTrips = null;
  }

  @Id
  @Nonnull
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", table = "user", unique = true, nullable = false)
  private final Long userId;

  @Nonnull
  @Column(name = "username", table = "user", unique = true, nullable = false)
  private final String username;

  @Nonnull
  @Column(name = "password", table = "user", nullable = false)
  private final String password;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  @OneToMany(
      mappedBy = "userEntity",
      targetEntity = RouteEntity.class,
      orphanRemoval = true,
      cascade = CascadeType.ALL)
  private final Collection<RouteEntity> savedRoutes;

  // Exclusion is because associations impact performance
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  @OneToMany(
      mappedBy = "userEntity",
      targetEntity = Trip.class,
      orphanRemoval = true,
      cascade = CascadeType.ALL)
  private final Collection<Trip> savedTrips;

  // Do not need to do it on the other entity, this table will be defined for both
  @Nonnull
  @ManyToMany(fetch = FetchType.EAGER, targetEntity = RoleEntity.class)
  @JoinTable(
      name = "user_role",
      schema = "user_data",
      joinColumns =
          @JoinColumn(
              name = "user_id",
              table = "user_data.role",
              referencedColumnName = "id",
              nullable = false,
              foreignKey = @ForeignKey(name = "user_role_user_id_fk")),
      inverseJoinColumns =
          @JoinColumn(
              name = "role_id",
              table = "role",
              referencedColumnName = "id",
              nullable = false,
              foreignKey = @ForeignKey(name = "user_role_role_id_fk")))
  private final Collection<RoleEntity> roleEntities;

  /**
   * Getter for {@code RoleEntity objects}.
   *
   * @return {@code RoleEntities} for a {@code UserEntity} or Empty Optional
   */
  public Optional<Collection<RoleEntity>> getRoleEntities() {
    return Optional.of(this.roleEntities);
  }
}
