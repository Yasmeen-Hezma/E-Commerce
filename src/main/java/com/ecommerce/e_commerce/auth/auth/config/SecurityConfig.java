package com.ecommerce.e_commerce.auth.auth.config;


import com.ecommerce.e_commerce.auth.auth.service.JwtFilter;
import com.ecommerce.e_commerce.auth.auth.service.UserDetailsService;
import com.ecommerce.e_commerce.core.user.enums.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static com.ecommerce.e_commerce.auth.auth.config.EndpointGroups.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final JwtFilter jwtFilter;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        // -------------------- PUBLIC ENDPOINTS--------------------
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                PUBLIC_GET_ENDPOINTS)
                        .permitAll()
                        // -------------------- SELLER ENDPOINTS--------------------
                        .requestMatchers(HttpMethod.POST,
                                SELLER_POST)
                        .hasRole(RoleEnum.SELLER.name())
                        .requestMatchers(HttpMethod.PATCH,
                                SELLER_PATCH)
                        .hasRole(RoleEnum.SELLER.name())
                        .requestMatchers(HttpMethod.DELETE,
                                SELLER_DELETE)
                        .hasRole(RoleEnum.SELLER.name())
                        // -------------------- CUSTOMER ENDPOINTS--------------------
                        .requestMatchers(CUSTOMER_ANY)
                        .hasRole(RoleEnum.CUSTOMER.name())
                        .requestMatchers(HttpMethod.POST,
                                CUSTOMER_POST)
                        .hasRole(RoleEnum.CUSTOMER.name())
                        .requestMatchers(HttpMethod.GET,
                                CUSTOMER_GET)
                        .hasRole(RoleEnum.CUSTOMER.name())
                        .requestMatchers(HttpMethod.PATCH,
                                CUSTOMER_PATCH)
                        .hasRole(RoleEnum.CUSTOMER.name())
                        .requestMatchers(HttpMethod.DELETE,
                                CUSTOMER_DELETE)
                        .hasRole(RoleEnum.CUSTOMER.name())
                        // -------------------- ADMIN ENDPOINTS--------------------
                        .requestMatchers(HttpMethod.POST
                                , ADMIN_POST)
                        .hasRole(RoleEnum.ADMIN.name())
                        .requestMatchers(HttpMethod.PATCH
                                , ADMIN_PATCH)
                        .hasRole(RoleEnum.ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE
                                , ADMIN_DELETE)
                        .hasRole(RoleEnum.ADMIN.name())
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout.logoutUrl("/auth/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler(((request, response, authentication) -> SecurityContextHolder.clearContext())));
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(new BCryptPasswordEncoder());
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
