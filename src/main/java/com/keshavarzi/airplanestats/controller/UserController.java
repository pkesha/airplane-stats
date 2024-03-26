//package com.keshavarzi.airplanestats.controller;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/login")
//public class UserController {
//    private final AuthenticationManager authenticationManager;
//
//    public UserController(AuthenticationManager authenticationManager) {
//        this.authenticationManager = authenticationManager;
//    }
//
//    @PostMapping("/user")
//    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest){
//        Authentication authenticationRequest =
//            UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.email(), loginRequest.password());
//        Authentication authenticationResponse = this.authenticationManager.authenticate(authenticationRequest);
//        return null;
//    }
//
//    public record LoginRequest(String email, String password) {}
//}
