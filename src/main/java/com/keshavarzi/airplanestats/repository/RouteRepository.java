package com.keshavarzi.airplanestats.repository;

import com.keshavarzi.airplanestats.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Long> {
    Route findRouteById(Long Id);

    List<Route> findAllByUser(Long userId);

    List<Route> findAllByTrip(Long tripId);

}
