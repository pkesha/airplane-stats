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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.Collection;

@Getter
@Setter
@ToString
@Entity
@Table(name = "trip", schema = "user_data")
public class Trip {

    @Id
    @Nonnull
    @Column(name = "id", table = "trip", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tripId;

    @Nonnull
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ManyToOne(targetEntity = UserEntity.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_email", referencedColumnName = "email", table = "trip", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "saved_routes_user_email_fk"))
    private UserEntity userEntity;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    @OneToMany(mappedBy = "trip", targetEntity = RouteEntity.class, orphanRemoval = true)
    private Collection<RouteEntity> routeEntities;

    @Column(name = "trip_name", table = "trip")
    private String tripName;

    @Column(name = "trip_start", table = "trip")
    private ZonedDateTime tripStart;

    @Column(name = "trip_end", table = "trip")
    private ZonedDateTime tripEnd;

//    @Nonnull
//    public UserEntity getUserEntity() throws CloneNotSupportedException {
//        return this.userEntity.clone();
//    }
//
//    public void setUserEntity(@Nonnull UserEntity userEntity) throws CloneNotSupportedException {
//        this.userEntity = userEntity.clone();
//    }
//
//    public Collection<RouteEntity> getRoutes() {
//        return List.copyOf(this.routeEntities);
//    }
//
//    public void setRoutes(Collection<RouteEntity> routeEntities) {
//        this.routeEntities = List.copyOf(routeEntities);
//    }

}
