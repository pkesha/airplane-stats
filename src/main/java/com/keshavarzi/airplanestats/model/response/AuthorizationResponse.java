package com.keshavarzi.airplanestats.model.response;

import lombok.Data;

/**
 * Authorization Response DTO.
 */
@Data
public final class AuthorizationResponse {
  private String accessToken;
  private String tokenType = "Bearer ";
  private String unauthorizedError;
}
