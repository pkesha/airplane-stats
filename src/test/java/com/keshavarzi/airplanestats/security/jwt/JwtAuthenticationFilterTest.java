package com.keshavarzi.airplanestats.security.jwt;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.keshavarzi.airplanestats.security.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@AutoConfigureMockMvc
@AutoConfigureWebMvc
@SpringBootTest(classes = JwtAuthenticationFilter.class)
class JwtAuthenticationFilterTest {
  private final HttpServletResponse response = new MockHttpServletResponse();
  @Spy private final HttpServletRequest request = new MockHttpServletRequest();
  @Spy private final FilterChain filterChain = new MockFilterChain();
  @Autowired MockMvc mockMvc;
  @Autowired WebApplicationContext webApplicationContext;
  @MockBean UserDetailsServiceImpl userDetailsService;
  @MockBean JwtUtility jwtUtility;
  @Autowired JwtAuthenticationFilter jwtAuthenticationFilter;

  @BeforeEach
  public void setUp() {
    this.jwtAuthenticationFilter =
        new JwtAuthenticationFilter(this.jwtUtility, this.userDetailsService);

    this.response.setHeader(
        JwtSecurityConstants.AUTHORIZATION_HEADER,
        JwtSecurityConstants.TOKEN_PREFIX_BEARER + " Test");

    this.mockMvc =
        MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
            .apply(springSecurity())
            .addFilter(this.jwtAuthenticationFilter)
            .build();
  }

  @Test
  void doFilterInternalServletException() throws ServletException, IOException {
    final String token = JwtSecurityConstants.TOKEN_PREFIX_BEARER + " Test";
    final String username = "validUsernameDoFilterInternalServletException@test.com";
    final String password = "validPass";
    final Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
    final GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("USER");
    grantedAuthorities.add(grantedAuthority);

    final UserDetails userDetails = new User(username, password, grantedAuthorities);

    Mockito.when(this.jwtUtility.getUsernameFromJwt(token)).thenReturn(username);

    Mockito.when(this.userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

    Mockito.doThrow(new ServletException())
        .when(this.filterChain)
        .doFilter(this.request, this.response);

    assertThrows(
        ServletException.class,
        () ->
            this.jwtAuthenticationFilter.doFilterInternal(
                this.request, this.response, this.filterChain));
  }

  @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
  @Test
  void doFilterInternalIOException() throws ServletException, IOException {
    final String token = JwtSecurityConstants.TOKEN_PREFIX_BEARER + " Test";
    final String username = "validUsernameDoFilterInternalIOException@test.com";
    final String password = "password";
    final Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
    final GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("USER");
    grantedAuthorities.add(grantedAuthority);

    final UserDetails userDetails = new User(username, password, grantedAuthorities);

    Mockito.when(this.jwtUtility.getUsernameFromJwt(token)).thenReturn(username);

    Mockito.when(this.userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

    Mockito.doThrow(new IOException()).when(this.filterChain).doFilter(this.request, this.response);

    assertThrows(
        IOException.class,
        () ->
            this.jwtAuthenticationFilter.doFilterInternal(
                this.request, this.response, this.filterChain));
  }

  @Test
  void doFilterInternalSuccess() {
    final String token = JwtSecurityConstants.TOKEN_PREFIX_BEARER + "test";
    final String username = "validUsernameDoFilterInternalIOException@test.com";
    final String password = "password";
    final Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
    final GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("USER");
    grantedAuthorities.add(grantedAuthority);
    final UserDetails userDetails = new User(username, password, grantedAuthorities);
    final TestingAuthenticationToken testingAuthenticationToken =
        new TestingAuthenticationToken(username, password);

    Mockito.when(this.request.getHeader(JwtSecurityConstants.AUTHORIZATION_HEADER))
        .thenReturn(token);

    testingAuthenticationToken.setDetails(
        new WebAuthenticationDetailsSource().buildDetails(this.request));

    // TODO why not passing in token???
    Mockito.when(this.jwtUtility.getUsernameFromJwt(anyString())).thenReturn(username);

    Mockito.when(this.userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

    assertDoesNotThrow(
        () ->
            this.jwtAuthenticationFilter.doFilterInternal(
                this.request, this.response, this.filterChain));
  }
}
