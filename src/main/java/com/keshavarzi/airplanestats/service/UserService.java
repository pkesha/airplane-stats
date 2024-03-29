//package com.keshavarzi.airplanestats.service;
//
//import com.keshavarzi.airplanestats.model.UserEntity;
//import com.keshavarzi.airplanestats.repository.UserEntityRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UserService {
//
//    private final UserEntityRepository userRepository;
//    @Autowired
//    public UserService(UserEntityRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    public UserEntity createUser (UserEntity newUser) {
//        UserEntity user = (UserEntity) SecurityContextHolder
//                .getContext()
//                .getAuthentication().
//                getPrincipal();
//        newUser.
//    }
//}
