package com.keshavarzi.airplanestats.model.request;

import lombok.Data;

/**
 * DTO Request to register.
 */
@Data
public final class RegisterRequest {
  private String email;
  private String password;
}
