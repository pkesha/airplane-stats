package com.keshavarzi.airplanestats.model.response;

import lombok.Data;

@Data
public final class AuthorizationResponse {
  private String accessToken;
  private String tokenType = "Bearer ";
  private String unauthorizedError;
}
