package com.keshavarzi.airplanestats.security.jwt;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;

class JwtSecurityConstants {

    @Value("${jwt.secret}")
    private static String JWT_SECRET;
    @Value("${jwt.expiration}")
    public static Long JWT_EXPIRATION;
    public static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer";

}
