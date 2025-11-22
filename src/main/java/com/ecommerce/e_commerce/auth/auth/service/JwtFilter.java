package com.ecommerce.e_commerce.auth.auth.service;

import com.ecommerce.e_commerce.auth.auth.utils.JwtUtils;
import com.ecommerce.e_commerce.auth.token.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import static com.ecommerce.e_commerce.auth.auth.config.EndpointGroups.PUBLIC_ENDPOINTS;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (isPermittedEndPoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = JwtUtils.extractTokenFromHeader(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            authenticationUserIfValid(token, request);
        } catch (Exception e) {
            throw new ServletException(e);
        }
        filterChain.doFilter(request, response);

    }

    private static final RequestMatcher[] PUBLIC_MATCHERS =
            Arrays.stream(PUBLIC_ENDPOINTS)
                    .map(AntPathRequestMatcher::new)
                    .toArray(RequestMatcher[]::new);

    private boolean isPermittedEndPoint(HttpServletRequest request) {
        for (RequestMatcher matcher : PUBLIC_MATCHERS) {
            if (matcher.matches(request)) {
                return true;
            }
        }
        return false;
    }
    private void authenticationUserIfValid(String token, HttpServletRequest request) {
        String userEmail = jwtService.extractUsername(token);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // retrieve the user from database
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(token, userDetails) && tokenService.isTokenValidInDB(token)) {
                authenticateUser(userDetails, request);
            }
        }
    }

    private void authenticateUser(UserDetails userDetails, HttpServletRequest request) {
        // if the token is valid, update the security context to have the authenticated user
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
