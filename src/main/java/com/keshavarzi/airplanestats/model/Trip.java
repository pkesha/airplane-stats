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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Data object representing plane-stats.user_data.trip table. */
@Getter
@Setter
@ToString
@NoArgsConstructor(force = true)
@Entity
@Table(name = "trip", schema = "user_data")
public final class Trip {
  @Id
  @Nonnull
  @Column(name = "id", table = "trip", unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private final Long tripId;

  @Nonnull
  @Setter(AccessLevel.NONE)
  @ManyToOne(targetEntity = UserEntity.class, cascade = CascadeType.ALL)
  @JoinColumn(
      name = "user_id",
      referencedColumnName = "id",
      table = "trip",
      nullable = false,
      unique = true,
      foreignKey = @ForeignKey(name = "trip_user_id_fk"))
  private final UserEntity userEntity;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  @ToString.Exclude
  @OneToMany(mappedBy = "trip", targetEntity = RouteEntity.class, orphanRemoval = true)
  private final Collection<RouteEntity> routeEntities;

  @Column(name = "trip_name", table = "trip")
  private final String tripName;

  @Column(name = "trip_start", table = "trip")
  private final ZonedDateTime tripStart;

  @Column(name = "trip_end", table = "trip")
  private final ZonedDateTime tripEnd;
}
