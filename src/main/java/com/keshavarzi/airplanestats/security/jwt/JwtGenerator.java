package com.keshavarzi.airplanestats.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import lombok.NonNull;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Component of Spring, JWT Utils.
 */
@Component
public class JwtGenerator {
  /**
   * Generates a token.
   *
   * @param authentication Authentication returned authentication manager when {@code
   *     UsernamePasswordAuthenticationToken} is added to the manager
   * @return Token based current data, expiration data, email, and secret key object.
   */
  public String generateToken(@NonNull final Authentication authentication) {
    String email = authentication.getName();
    Date currentDate = new Date();
    Date expireDate = new Date(currentDate.getTime() + JwtSecurityConstants.JWT_EXPIRATION);

    return Jwts.builder()
        .subject(email)
        .issuedAt(currentDate)
        .expiration(expireDate)
        .signWith(JwtSecurityConstants.SECRET_KEY)
        .compact();
  }

  /**
   * Returns email from a token.
   *
   * @param token Used for parsing
   * @return User's email
   */
  protected String getEmailFromJwt(@NonNull final String token) {

    Claims claims =
        Jwts.parser()
            .verifyWith(JwtSecurityConstants.SECRET_KEY)
            .build()
            .parseSignedClaims(token)
            .getPayload();

    return claims.getSubject();
  }

  /**
   * Will validate a token.
   *
   * @param token Checked for validation
   */
  protected void validateToken(@NonNull final String token) {
    try {
      Jwts.parser().verifyWith(JwtSecurityConstants.SECRET_KEY).build().parseSignedClaims(token);
    } catch (Exception exception) {
      throw new AuthenticationCredentialsNotFoundException("JWT Expired or incorrect");
    }
  }
}
