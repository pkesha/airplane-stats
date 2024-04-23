package com.keshavarzi.airplanestats.model.request;

import lombok.Data;

@Data
public final class LoginRequest {
  private String email;
  private String password;
}
