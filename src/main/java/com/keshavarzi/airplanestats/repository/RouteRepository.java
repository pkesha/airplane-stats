package com.keshavarzi.airplanestats.repository;

import com.keshavarzi.airplanestats.model.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface to search plane-stats.user_data.role table
 */
public interface RouteRepository extends JpaRepository<RouteEntity, Long> {}
