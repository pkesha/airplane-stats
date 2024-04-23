package com.keshavarzi.airplanestats.repository;

import com.keshavarzi.airplanestats.model.UserEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findUserEntityByEmail(@NonNull final String email);
}
