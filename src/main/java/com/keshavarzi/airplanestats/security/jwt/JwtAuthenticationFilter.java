package com.keshavarzi.airplanestats.security.jwt;

import com.keshavarzi.airplanestats.security.service.UserDetailsServiceImpl;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtGenerator jwtGenerator;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;


    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     * This is a form of middleware.
     * The request will be intercepted and will be filtered for correct credentials
     * It sets the AuthenticationToken, if it's valid token, to the SecurityContext
     * The token will be associated with email and retrieve the {@code UserDetails}
     *
     * @param request HTTP request to filter for correct credentials
     * @param response HTTP with a response
     * @param filterChain Passed in, will use method {@code doFilter}, to continue the filtering when code is done
     */
    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain)
            throws ServletException, IOException {
        String token = this.getJwtFromRequest(request);
        if(StringUtils.hasText(token) && this.jwtGenerator.validateToken(token)) {
            String email = this.jwtGenerator.getEmailFromJwt(token);
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, userDetails.getAuthorities());
            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource()
                    .buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Gets the JWT bearer token from the request
     * @param request The request to check for bearer token
     * @return Bearer token to check
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(JwtSecurityConstants.AUTHORIZATION_HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtSecurityConstants.TOKEN_PREFIX)) {
            return bearerToken.substring(7);
        } else {
            return null;
        }
    }
}