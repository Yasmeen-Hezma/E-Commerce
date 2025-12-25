package com.ecommerce.e_commerce.security.auth.service;

import com.ecommerce.e_commerce.security.auth.enums.RoleEnum;
import com.ecommerce.e_commerce.security.auth.model.AuthUser;
import com.ecommerce.e_commerce.security.auth.model.Role;
import com.ecommerce.e_commerce.security.token.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {
    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private TokenService tokenService;
    @Mock
    private HttpServletRequest httpRequest;
    @Mock
    private HttpServletResponse httpResponse;
    @Mock
    private FilterChain filterChain;
    @InjectMocks
    private JwtFilter jwtFilter;

    private AuthUser authUser;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        Role customerRole = Role
                .builder()
                .roleId(1L)
                .roleEnum(RoleEnum.CUSTOMER)
                .build();

        authUser = AuthUser
                .builder()
                .id(1L)
                .email("test@email.com")
                .password("encodedPassword")
                .roles(Collections.singleton(customerRole))
                .build();

        userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("test@email.com")
                .password("encodedPassword")
                .authorities(new SimpleGrantedAuthority(RoleEnum.CUSTOMER.toString()))
                .build();

        // Clear security context before each test
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_ShouldSkipAuthentication_WhenPublicEndpoint() throws ServletException, IOException {
        // Arrange
        when(httpRequest.getServletPath()).thenReturn("api/v1/auth/login");
        // Act
        jwtFilter.doFilterInternal(httpRequest, httpResponse, filterChain);
        // Assert
        verify(filterChain).doFilter(httpRequest, httpResponse);
        verify(jwtService, never()).extractUsername(any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_ShouldAuthenticateUser_WhenValidToken() throws ServletException, IOException {
        // Arrange
        String token = "valid.jwt.token";
        when(httpRequest.getServletPath()).thenReturn("/api/v1/orders");
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn("test@email.com");
        when(userDetailsService.loadUserByUsername("test@email.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);
        when(tokenService.isTokenValidInDB(token)).thenReturn(true);
        // Act
        jwtFilter.doFilterInternal(httpRequest, httpResponse, filterChain);
        //Assert
        verify(filterChain).doFilter(httpRequest, httpResponse);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("test@email.com", SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void doFilterInternal_ShouldNotAuthenticateUser_WhenInvalidToken() throws ServletException, IOException {
        // Arrange
        String token = "invalid.jwt.token";
        when(httpRequest.getServletPath()).thenReturn("/api/v1/orders");
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn("test@email.com");
        when(userDetailsService.loadUserByUsername("test@email.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(false);
        // Act
        jwtFilter.doFilterInternal(httpRequest, httpResponse, filterChain);
        //Assert
        verify(filterChain).doFilter(httpRequest, httpResponse);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}