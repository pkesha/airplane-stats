package com.keshavarzi.airplanestats.model.request;

import lombok.Data;

/**
 * DTO Request for logging in.
 */
@Data
public final class LoginRequest {
  private String email;
  private String password;
}
