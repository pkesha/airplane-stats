package com.keshavarzi.airplanestats.repository;

import com.keshavarzi.airplanestats.model.UserEntity;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JpaRepository interface for querying plane-stats.user_data.user table
 */
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findUserEntityByEmail(@NonNull final String email);
}
