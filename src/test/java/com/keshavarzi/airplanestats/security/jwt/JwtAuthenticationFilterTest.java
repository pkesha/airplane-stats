package com.keshavarzi.airplanestats.security.jwt;

import com.keshavarzi.airplanestats.security.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AutoConfigureMockMvc
@AutoConfigureWebMvc
@SpringBootTest(classes = JwtAuthenticationFilter.class)
class JwtAuthenticationFilterTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext webApplicationContext;
    @MockBean
    JwtGenerator jwtGenerator;
    @MockBean
    UserDetailsServiceImpl userDetailsService;
    @InjectMocks
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Spy
    private HttpServletRequest request = new MockHttpServletRequest();
    private final HttpServletResponse response = new MockHttpServletResponse();
    @Spy
    private FilterChain filterChain = new MockFilterChain();

    @BeforeEach
    public void setUp() {
        this.response.setHeader(JwtSecurityConstants.AUTHORIZATION_HEADER, JwtSecurityConstants.TOKEN_PREFIX + " Test");
    }

    @Test
    void doFilterInternalServletException() throws ServletException, IOException {
        String token = JwtSecurityConstants.TOKEN_PREFIX + " Test";
        String email = "validEmailDoFilterInternalServletException@test.com";
        String password = "password";
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("USER");
        grantedAuthorities.add(grantedAuthority);

        UserDetails userDetails = new User(email, password, grantedAuthorities);

        Mockito.when(this.jwtGenerator.getEmailFromJwt(token))
                .thenReturn(email);

        Mockito.when(this.userDetailsService.loadUserByUsername(email))
                .thenReturn(userDetails);

        Mockito.doThrow(new ServletException())
                .when(this.filterChain)
                .doFilter(this.request, this.response);

        assertThrows(ServletException.class, () ->
                this.jwtAuthenticationFilter.doFilterInternal(this.request, this.response, this.filterChain));

    }

    @Test
    void doFilterInternalIOException() throws ServletException, IOException {
        String token = JwtSecurityConstants.TOKEN_PREFIX + " Test";
        String email = "validEmailDoFilterInternalIOException@test.com";
        String password = "password";
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("USER");
        grantedAuthorities.add(grantedAuthority);

        UserDetails userDetails = new User(email, password, grantedAuthorities);

        Mockito.when(this.jwtGenerator.getEmailFromJwt(token))
                .thenReturn(email);

        Mockito.when(this.userDetailsService.loadUserByUsername(email))
                .thenReturn(userDetails);

        Mockito.doThrow(new IOException())
                .when(this.filterChain)
                .doFilter(this.request, this.response);

        assertThrows(IOException.class, () ->
                this.jwtAuthenticationFilter.doFilterInternal(this.request, this.response, this.filterChain));

    }

    @Test
    void doFilterInternalSuccess() {
        String token = JwtSecurityConstants.TOKEN_PREFIX + " Test";
        String email = "validEmailDoFilterInternalIOException@test.com";
        String password = "password";
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("USER");
        grantedAuthorities.add(grantedAuthority);

        UserDetails userDetails = new User(email, password, grantedAuthorities);

        Mockito.when(this.jwtGenerator.getEmailFromJwt(token))
                .thenReturn(email);

        Mockito.when(this.userDetailsService.loadUserByUsername(email))
                .thenReturn(userDetails);

        assertDoesNotThrow(() ->
                this.jwtAuthenticationFilter.doFilterInternal(this.request, this.response, this.filterChain));

    }



}