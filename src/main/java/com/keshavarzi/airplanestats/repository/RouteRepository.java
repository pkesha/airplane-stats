package com.keshavarzi.airplanestats.repository;

import com.keshavarzi.airplanestats.model.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<RouteEntity, Long> {}
