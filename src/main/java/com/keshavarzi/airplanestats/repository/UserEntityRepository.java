package com.keshavarzi.airplanestats.repository;

import com.keshavarzi.airplanestats.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEntityRepository extends JpaRepository<UserEntity, String> {

    UserEntity findUserEntityByEmail(String email);

    boolean deleteUserEntityByEmail(String email);

    boolean updateUserEntityByEmail(String email);

}
