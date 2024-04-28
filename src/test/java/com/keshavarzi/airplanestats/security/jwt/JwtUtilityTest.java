package com.keshavarzi.airplanestats.security.jwt;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.jsonwebtoken.Jwts;
import java.util.Date;
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
  @Autowired MockMvc mockMvc;
  @Autowired WebApplicationContext webApplicationContext;
  @MockBean AuthenticationManager authenticationManager;
  @Autowired JwtUtility jwtUtility;

  private String generateToken(Authentication authentication) {
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

  @Test
  void generateTokenTest() {
    String email = "validEmailGenerateTokenTest@test.com";
    String password = "validPass";
    Authentication authentication = new TestingAuthenticationToken(email, password);

    Mockito.when(
            this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)))
        .thenReturn(authentication);

    String tokenExpected = this.generateToken(authentication);
    String tokenActual = this.jwtUtility.generateToken(authentication);

    assertEquals(tokenExpected, tokenActual);
  }

  @Test
  void generateDifferentTokenWithDifferentEmailTest() {
    String email0 = "validEmailGenerateDifferentTokenWithDifferentEmailTest@test.com";
    String password0 = "validPass0";

    String email1 = "validEmail@test.com";
    String password1 = "validPass1";

    Authentication authentication0 = new TestingAuthenticationToken(email0, password0);
    Authentication authentication1 = new TestingAuthenticationToken(email1, password1);

    Mockito.when(
            this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email0, password0)))
        .thenReturn(authentication0);

    Mockito.when(
            this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email1, password1)))
        .thenReturn(authentication1);

    assertNotEquals(authentication0, authentication1);
  }

  @Test
  void getEmailFromJwt() {
    String email = "validEmailGetEmailFromJwt";
    String password = "validPass";
    Authentication authentication = new TestingAuthenticationToken(email, password);

    Mockito.when(
            this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)))
        .thenReturn(authentication);

    String tokenExpected = this.generateToken(authentication);

    assertEquals(email, this.jwtUtility.getEmailFromJwt(tokenExpected));
  }

  @Test
  void emailsAreDifferentFromJwtTest() {
    String email0 = "validEmailEmailsAreDifferentFromJwtTest@test.com";
    String password0 = "validPass0";

    String email1 = "validEmail@test.com";
    String password1 = "validPass1";

    Authentication authentication0 = new TestingAuthenticationToken(email0, password0);
    Authentication authentication1 = new TestingAuthenticationToken(email1, password1);

    Mockito.when(
            this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email0, password0)))
        .thenReturn(authentication0);

    Mockito.when(
            this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email1, password1)))
        .thenReturn(authentication1);

    String token0 = this.generateToken(authentication0);
    String token1 = this.generateToken(authentication1);

    String tokenEmail0 = this.jwtUtility.getEmailFromJwt(token0);
    String tokenEmail1 = this.jwtUtility.getEmailFromJwt(token1);

    assertNotEquals(tokenEmail0, tokenEmail1);
  }

  @Test
  void validatedTokenSuccess() {
    String email0 = "validatedTokenSuccess@test.com";
    String password0 = "validPass0";
    Authentication authentication0 = new TestingAuthenticationToken(email0, password0);
    String token = this.generateToken(authentication0);

    assertDoesNotThrow(() -> this.jwtUtility.validateToken(token));
  }

  @Test
  void validateTokenFails() {
    String email = "validEmailValidatedToken@test.com";
    String password = "validPass";
    String token = "invalidToken";
    Authentication authentication = new TestingAuthenticationToken(email, password);

    Mockito.when(
            this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)))
        .thenReturn(authentication);

    assertThrows(Exception.class, () -> this.jwtUtility.validateToken(token));
  }
}