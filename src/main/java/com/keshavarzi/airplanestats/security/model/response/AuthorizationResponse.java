package com.keshavarzi.airplanestats.security.model.response;

/** DTO for Authorization Information. */
public record AuthorizationResponse(String accessToken, String tokenType, String message) {}
