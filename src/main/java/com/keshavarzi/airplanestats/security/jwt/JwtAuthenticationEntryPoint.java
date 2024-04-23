package com.keshavarzi.airplanestats.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Commences and authentication scheme that sends a response error.
 */
@Component
public final class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
  /**
   * Commences an authentication scheme.
   *
   * <p><code>ExceptionTranslationFilter</code> will populate the <code>HttpSession</code> attribute
   * named <code>AbstractAuthenticationProcessingFilter.SPRING_SECURITY_SAVED_REQUEST_KEY</code>
   * with the requested target URL before calling this method.
   *
   * <p>Implementations should modify the headers on the <code>ServletResponse</code> as necessary
   * to commence the authentication process.
   *
   * @param request that resulted in an <code>AuthenticationException</code>
   * @param response so that the user agent can begin authentication
   * @param authException that caused the invocation
   */
  @Override
  public void commence(
      @NonNull final HttpServletRequest request,
      @NonNull final HttpServletResponse response,
      @NonNull final AuthenticationException authException)
      throws IOException {
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
  }
}
