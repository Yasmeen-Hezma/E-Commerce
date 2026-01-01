package com.ecommerce.e_commerce.security.auth.service;

import com.ecommerce.e_commerce.common.exception.ItemNotFoundException;
import com.ecommerce.e_commerce.security.auth.enums.RoleEnum;
import com.ecommerce.e_commerce.security.auth.model.AuthUser;
import com.ecommerce.e_commerce.security.auth.model.Role;
import com.ecommerce.e_commerce.security.auth.repository.AuthUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Optional;

import static com.ecommerce.e_commerce.common.utils.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceTest {
    @Mock
    private AuthUserRepository authUserRepository;

    @InjectMocks
    private UserDetailsService userDetailsService;
    private AuthUser authUser;

    @BeforeEach
    void setUp() {
        Role customerRole = Role
                .builder()
                .roleId(1L)
                .roleEnum(RoleEnum.CUSTOMER)
                .build();
        authUser = AuthUser
                .builder()
                .userId(1L)
                .email("test@email.com")
                .password("encodedPassword")
                .roles(Collections.singleton(customerRole))
                .build();
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        // Arrange
        when(authUserRepository.findByEmail("test@email.com")).thenReturn(Optional.of(authUser));
        // Act
        UserDetails result = userDetailsService.loadUserByUsername("test@email.com");
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("test@email.com");
        verify(authUserRepository).findByEmail("test@email.com");
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(authUserRepository.findByEmail("not-found@email.com")).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("not-found@email.com"))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining(USER_NOT_FOUND);
        verify(authUserRepository).findByEmail("not-found@email.com");
    }
}