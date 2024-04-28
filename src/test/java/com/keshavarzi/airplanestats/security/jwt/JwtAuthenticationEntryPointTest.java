package com.keshavarzi.airplanestats.security.jwt;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.context.WebApplicationContext;

@AutoConfigureWebMvc
@SpringBootTest(classes = JwtAuthenticationEntryPoint.class)
@TestComponent(value = "JwtAuthenticationEntryPointTest")
class JwtAuthenticationEntryPointTest {
  private final AuthenticationException authException =
      new AuthenticationServiceException("Random Implementation of AuthenticationException");
  @Mock private final MockHttpServletRequest request = new MockHttpServletRequest();
  @Mock private final MockHttpServletResponse response = new MockHttpServletResponse();
  @Autowired WebApplicationContext webApplicationContext;
  @MockBean DispatcherServletPath dispatcherServletPath;
  @Autowired JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  @Test
  void throwsIOException() throws IOException {
    Mockito.doThrow(new IOException())
        .when(response)
        .sendError(HttpServletResponse.SC_UNAUTHORIZED, this.authException.getMessage());

    Assertions.assertThrows(
        IOException.class,
        () ->
            this.jwtAuthenticationEntryPoint.commence(
                this.request, this.response, this.authException));
  }

  @Test
  void successful() throws IOException {
    Mockito.doNothing()
        .when(this.response)
        .sendError(HttpServletResponse.SC_UNAUTHORIZED, this.authException.getMessage());

    Assertions.assertDoesNotThrow(
        () ->
            this.jwtAuthenticationEntryPoint.commence(
                this.request, this.response, this.authException));
  }
}
