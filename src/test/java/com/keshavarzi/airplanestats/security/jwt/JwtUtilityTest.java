package com.keshavarzi.airplanestats.security.jwt;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.jsonwebtoken.Jwts;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@AutoConfigureMockMvc
@AutoConfigureWebMvc
@SpringBootTest(classes = JwtUtility.class)
class JwtUtilityTest {
  private static final String validUsername0 = "validEmail0@test.com";
  private static final String validPassword0 = "validPassword0";
  private static final String validUsername1 = "validEmail1@test.com";
  private static final String validPassword1 = "validPassword1";
  @Autowired MockMvc mockMvc;
  @Autowired WebApplicationContext webApplicationContext;
  @MockBean AuthenticationManager authenticationManager;
  @Autowired JwtUtility jwtUtility;
  Authentication authentication0 = new TestingAuthenticationToken(validUsername0, validPassword1);

  Authentication authentication1 = new TestingAuthenticationToken(validUsername1, validPassword0);

  private String generateToken(Authentication authentication) {
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

  @BeforeEach
  void setUp() {
    Mockito.when(
            this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(validUsername0, validPassword0)))
        .thenReturn(this.authentication0);

    Mockito.when(
            this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(validUsername1, validPassword1)))
        .thenReturn(authentication1);
  }

  @Test
  void generateTokenTest() {
    String tokenExpected = this.generateToken(this.authentication0);
    String tokenActual = this.jwtUtility.generateToken(this.authentication0);

    assertEquals(tokenExpected, tokenActual);
  }

  @Test
  void generateDifferentTokenWithDifferentUsername() {
    assertNotEquals(this.authentication0, this.authentication1);
  }

  @Test
  void getUsernameFromJwt() {
    String tokenExpected = this.generateToken(this.authentication0);

    assertEquals(validUsername0, this.jwtUtility.getUsernameFromJwt(tokenExpected));
  }

  @Test
  void usernamesAreDifferentFromJwtTest() {
    String token0 = this.generateToken(this.authentication0);
    String token1 = this.generateToken(this.authentication1);

    String tokenUsername0 = this.jwtUtility.getUsernameFromJwt(token0);
    String tokenUsername1 = this.jwtUtility.getUsernameFromJwt(token1);

    assertNotEquals(tokenUsername0, tokenUsername1);
  }

  @Test
  void validatedTokenSuccess() {
    String token = this.generateToken(this.authentication0);

    assertDoesNotThrow(() -> this.jwtUtility.validateToken(token));
  }

  @Test
  void validateTokenFails() {
    String token = "invalidToken";

    assertThrows(Exception.class, () -> this.jwtUtility.validateToken(token));
  }
}
