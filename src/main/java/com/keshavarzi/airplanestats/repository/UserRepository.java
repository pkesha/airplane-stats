package com.keshavarzi.airplanestats.repository;

import com.keshavarzi.airplanestats.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    User findUserByEmail(String email);

    boolean deleteUserByEmail(String email);

    boolean updateUserByEmail(String email);

}
