package com.keshavarzi.airplanestats.model.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
}
