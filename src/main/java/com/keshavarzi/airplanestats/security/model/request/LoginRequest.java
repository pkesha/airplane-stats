package com.keshavarzi.airplanestats.security.model.request;

/** DTO Request for logging in. */
public record LoginRequest(String username, String password, String token) {}
