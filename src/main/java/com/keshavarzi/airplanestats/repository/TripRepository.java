package com.keshavarzi.airplanestats.repository;

import com.keshavarzi.airplanestats.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findAllByUser(String email);

    Trip findTripById(Long Id);

    List<Trip> findTripByRoutes(Long routeId);

}
