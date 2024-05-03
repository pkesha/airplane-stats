package com.keshavarzi.airplanestats.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import lombok.NonNull;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/** Component of Spring, JWT Utils. */
@Component
public class JwtUtility {
  /**
   * Generates a token.
   *
   * @param authentication Authentication returned authentication manager when {@code
   *     UsernamePasswordAuthenticationToken} is added to the manager
   * @return Token based current data, expiration data, username, and secret key object.
   */
  public final String generateToken(@NonNull final Authentication authentication) {
    String username = authentication.getName();
    Date currentDate = new Date();
    Date expireDate = new Date(currentDate.getTime() + JwtSecurityConstants.JWT_EXPIRATION);

    return Jwts.builder()
        .subject(username)
        .issuedAt(currentDate)
        .expiration(expireDate)
        .signWith(JwtSecurityConstants.SECRET_KEY, Jwts.SIG.HS512)
        .compact();
  }

  /**
   * Returns username from a token.
   *
   * @param token Used for parsing
   * @return User's username
   */
  protected String getUsernameFromJwt(@NonNull final String token) {

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
  protected final void validateToken(@NonNull final String token) {
    try {
      Jwts.parser()
          .verifyWith(JwtSecurityConstants.SECRET_KEY)
          .build()
          .parseSignedClaims(token)
          .getPayload();
    } catch (Exception exception) {
      throw new AuthenticationCredentialsNotFoundException("JWT Expired or incorrect: " + token);
    }
  }
}
