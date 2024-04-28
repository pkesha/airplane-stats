package com.keshavarzi.airplanestats.security.model.request;

import lombok.Data;

/** DTO Request for logging in. */
@Data
public final class LoginRequest {
  private String email;
  private String password;
}
