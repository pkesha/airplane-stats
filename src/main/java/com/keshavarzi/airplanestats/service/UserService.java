//package com.keshavarzi.airplanestats.service;
//
//import com.keshavarzi.airplanestats.model.User;
//import com.keshavarzi.airplanestats.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UserService {
//
//    private final UserRepository userRepository;
//    @Autowired
//    public UserService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    public User createUser (User newUser) {
//        User user = (User) SecurityContextHolder
//                .getContext()
//                .getAuthentication().
//                getPrincipal();
//        newUser.
//    }
//}
