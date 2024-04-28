package com.keshavarzi.airplanestats.security.jwt;

import com.keshavarzi.airplanestats.security.service.UserDetailsServiceImpl;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/** Add implementation to filter JWT tokens. */
public final class JwtAuthenticationFilter extends OncePerRequestFilter {
  @Nonnull private final JwtUtility jwtUtility;
  @Nonnull private final UserDetailsServiceImpl userDetailsService;

  @Autowired
  public JwtAuthenticationFilter(
      @Nonnull final JwtUtility jwtUtility,
      @Nonnull final UserDetailsServiceImpl userDetailsService) {
    this.jwtUtility = jwtUtility;
    this.userDetailsService = userDetailsService;
  }

  /**
   * Same contract as for {@code doFilter}, but guaranteed to be just invoked once per request
   * within a single request thread. See {@link #shouldNotFilterAsyncDispatch()} for details.
   *
   * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the default
   * ServletRequest and ServletResponse ones. This is a form of middleware. The request will be
   * intercepted and will be filtered for correct credentials It sets the AuthenticationToken, if
   * it's valid token, to the SecurityContext The token will be associated with username and
   * retrieve the {@code UserDetails}
   *
   * @param request HTTP request to filter for correct credentials
   * @param response HTTP with a response
   * @param filterChain Passed in, will use method {@code doFilter}, to continue the filtering when
   *     code is done
   */
  @Override
  protected void doFilterInternal(
      @Nonnull final HttpServletRequest request,
      @Nonnull final HttpServletResponse response,
      @Nonnull final FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String token = this.getJwtFromRequest(request);
      this.jwtUtility.validateToken(token);
      String username = this.jwtUtility.getUsernameFromJwt(token);
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
      UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      usernamePasswordAuthenticationToken.setDetails(
          new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    } catch (
    AuthenticationCredentialsNotFoundException authenticationCredentialsNotFoundException) {
      System.out.println(
          "Authentication failed: " + authenticationCredentialsNotFoundException.getMessage());
    } finally {
      filterChain.doFilter(request, response);
    }
  }

  /**
   * Gets the JWT bearer token from the request.
   *
   * @param request The request to check for bearer token
   * @return Bearer token to check
   */
  private String getJwtFromRequest(@Nonnull final HttpServletRequest request) {
    String bearerToken = request.getHeader(JwtSecurityConstants.AUTHORIZATION_HEADER);
    if (StringUtils.hasText(bearerToken)
        && bearerToken.startsWith(JwtSecurityConstants.TOKEN_PREFIX)) {
      return bearerToken.substring(7);
    } else {
      return JwtSecurityConstants.NO_BEARER;
    }
  }
}
