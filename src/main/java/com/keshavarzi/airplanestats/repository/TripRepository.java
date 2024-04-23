package com.keshavarzi.airplanestats.repository;

import com.keshavarzi.airplanestats.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {}
