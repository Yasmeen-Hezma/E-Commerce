package com.ecommerce.e_commerce.user.profile.service;

import com.ecommerce.e_commerce.commerce.order.dto.UserAddressDto;
import com.ecommerce.e_commerce.common.exception.DuplicateItemException;
import com.ecommerce.e_commerce.common.exception.ItemNotFoundException;
import com.ecommerce.e_commerce.common.exception.UnauthorizedException;
import com.ecommerce.e_commerce.security.auth.dto.CustomerRegistrationRequest;
import com.ecommerce.e_commerce.security.auth.dto.LoginRequest;
import com.ecommerce.e_commerce.security.auth.dto.RegisterRequest;
import com.ecommerce.e_commerce.security.auth.dto.SellerRegistrationRequest;
import com.ecommerce.e_commerce.security.auth.enums.RoleEnum;
import com.ecommerce.e_commerce.security.auth.enums.SellerType;
import com.ecommerce.e_commerce.security.auth.model.AuthUser;
import com.ecommerce.e_commerce.security.auth.model.Role;
import com.ecommerce.e_commerce.security.auth.repository.AuthUserRepository;
import com.ecommerce.e_commerce.security.auth.repository.RoleRepository;
import com.ecommerce.e_commerce.security.auth.service.EmailVerificationService;
import com.ecommerce.e_commerce.security.auth.service.JwtService;
import com.ecommerce.e_commerce.user.profile.model.User;
import com.ecommerce.e_commerce.user.profile.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static com.ecommerce.e_commerce.common.utils.Constants.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthUserRepository authUserRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private EmailVerificationService emailVerificationService;
    @Mock
    private JwtService jwtService;
    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private UserServiceImpl userService;

    private Role sellerRole;
    private Role customerRole;
    private RegisterRequest customerRegisterRequest;
    private RegisterRequest sellerRegisterRequest;
    private LoginRequest loginRequest;
    private User user;
    private AuthUser authUser;

    @BeforeEach
    void setUp() {
        sellerRole = Role
                .builder()
                .roleId(1L)
                .roleEnum(RoleEnum.SELLER)
                .build();
        customerRole = Role
                .builder()
                .roleId(1L)
                .roleEnum(RoleEnum.CUSTOMER)
                .build();
        user = User.builder()
                .userId(1L)
                .firstName("Yousef")
                .lastName("Mahmoud")
                .phone("01098765432")
                .phoneCode(20)
                .governorate("Cairo")
                .city("Cairo")
                .street("Street 1")
                .floorNumber("3")
                .apartmentNumber("12B")
                .build();
        authUser = AuthUser
                .builder()
                .userId(1L)
                .user(user)
                .email("Yousef@email.com")
                .password("encodedPassword")
                .roles(Collections.singleton(customerRole))
                .lastLogin(Instant.now())
                .build();
        user.setAuthUser(authUser);

        CustomerRegistrationRequest customer = CustomerRegistrationRequest
                .builder()
                .firstName("Yousef")
                .lastName("Mahmoud")
                .phone("01098765432")
                .phoneCode(20)
                .build();
        customerRegisterRequest = RegisterRequest
                .builder()
                .customer(customer)
                .role(RoleEnum.CUSTOMER)
                .verifiedEmail("Yousef@email.com")
                .password("password123")
                .build();
        SellerRegistrationRequest seller = SellerRegistrationRequest
                .builder()
                .sellerType(SellerType.INDIVIDUAL)
                .brandName("Nike")
                .businessAddress("business st 123")
                .shippingZone("Cairo")
                .build();
        sellerRegisterRequest = RegisterRequest
                .builder()
                .seller(seller)
                .role(RoleEnum.SELLER)
                .verifiedEmail("seller@email.com")
                .password("password123")
                .build();
        loginRequest = LoginRequest
                .builder()
                .email("Yousef@email.com")
                .password("password123")
                .build();
    }

    @Test
    void createUser_ShouldCreateCustomer_WhenValidCustomerRequest() {
        // Arrange
        when(emailVerificationService.isEmailVerified("Yousef@email.com")).thenReturn(true);
        when(authUserRepository.findByEmail("Yousef@email.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(roleRepository.findByRoleEnum(RoleEnum.CUSTOMER)).thenReturn(Optional.of(customerRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(authUserRepository.save(any(AuthUser.class))).thenReturn(authUser);
        // Act
        AuthUser result = userService.createUser(customerRegisterRequest);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("Yousef@email.com");
        assertThat(result.getUser()).isNotNull();
        assertThat(result.getRoles()).contains(customerRole);

        verify(emailVerificationService).isEmailVerified("Yousef@email.com");
        verify(authUserRepository).findByEmail("Yousef@email.com");
        verify(userRepository).save(any(User.class));
        verify(roleRepository, times(2)).findByRoleEnum(RoleEnum.CUSTOMER);
        verify(passwordEncoder).encode("password123");
        verify(authUserRepository).save(any(AuthUser.class));
    }

    @Test
    void createUser_ShouldCreateSeller_WhenValidSellerRequest() {
        // Arrange
        User sellerUser = User.builder()
                .userId(2L)
                .brandName("Nike")
                .sellerType(SellerType.INDIVIDUAL)
                .businessAddress("Business St 123")
                .shippingZone("Cairo")
                .build();

        when(emailVerificationService.isEmailVerified("seller@email.com")).thenReturn(true);
        when(authUserRepository.findByEmail("seller@email.com")).thenReturn(Optional.empty());
        when(roleRepository.findByRoleEnum(RoleEnum.SELLER)).thenReturn(Optional.of(sellerRole));
        when(userRepository.save(any(User.class))).thenReturn(sellerUser);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(authUserRepository.save(any(AuthUser.class))).thenAnswer(invocation -> {
            AuthUser savedAuthUser = invocation.getArgument(0);
            savedAuthUser.setUserId(2L);
            return savedAuthUser;
        });

        // Act
        AuthUser result = userService.createUser(sellerRegisterRequest);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(2L);
        assertThat(result.getEmail()).isEqualTo("seller@email.com");
        assertThat(result.getUser()).isNotNull();
        assertThat(result.getRoles()).contains(sellerRole);

        verify(emailVerificationService).isEmailVerified("seller@email.com");
        verify(authUserRepository).findByEmail("seller@email.com");
        verify(roleRepository, times(2)).findByRoleEnum(RoleEnum.SELLER);
        verify(userRepository).save(any(User.class));
        verify(authUserRepository).save(any(AuthUser.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        when(emailVerificationService.isEmailVerified("Yousef@email.com")).thenReturn(true);
        when(authUserRepository.findByEmail("Yousef@email.com")).thenReturn(Optional.of(authUser));
        //Act & Assert
        assertThatThrownBy(() -> userService.createUser(customerRegisterRequest))
                .isInstanceOf(DuplicateItemException.class)
                .hasMessageContaining(EMAIL_ALREADY_EXISTS);
        verify(emailVerificationService).isEmailVerified("Yousef@email.com");
        verify(authUserRepository).findByEmail("Yousef@email.com");
        verify(userRepository, never()).save(any(User.class));
        verify(authUserRepository, never()).save(any(AuthUser.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailNotVerified() {
        // Arrange
        when(emailVerificationService.isEmailVerified("Yousef@email.com")).thenReturn(false);
        //Act & Assert
        assertThatThrownBy(() -> userService.createUser(customerRegisterRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining(EMAIL_IS_NOT_VERIFIED);
        verify(emailVerificationService).isEmailVerified("Yousef@email.com");
        verify(userRepository, never()).save(any(User.class));
        verify(authUserRepository, never()).save(any(AuthUser.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenRoleNotFound() {
        // Arrange
        when(emailVerificationService.isEmailVerified("Yousef@email.com")).thenReturn(true);
        when(authUserRepository.findByEmail("Yousef@email.com")).thenReturn(Optional.empty());
        when(roleRepository.findByRoleEnum(RoleEnum.CUSTOMER)).thenReturn(Optional.empty());
        //Act & Assert
        assertThatThrownBy(() -> userService.createUser(customerRegisterRequest))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining(ROLE_NOT_FOUND);
        verify(emailVerificationService).isEmailVerified("Yousef@email.com");
        verify(authUserRepository).findByEmail("Yousef@email.com");
        verify(roleRepository).findByRoleEnum(RoleEnum.CUSTOMER);
        verify(userRepository, never()).save(any(User.class));
        verify(authUserRepository, never()).save(any(AuthUser.class));
    }

    @Test
    void getUserId_ShouldReturnUserId_WhenValidToken() {
        // Arrange
        String token = "valid.jwt.token";
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);
        // Act
        Long result = userService.getUserId(httpRequest);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(1L);
        verify(httpRequest).getHeader("Authorization");
        verify(jwtService).extractUserIdFromToken(token);
    }

    @Test
    void getUserByRequest_ShouldReturnUser_WhenValidRequest() {
        // Arrange
        String token = "valid.jwt.token";
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        // Act
        User result = userService.getUserByRequest(httpRequest);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        verify(httpRequest).getHeader("Authorization");
        verify(jwtService).extractUserIdFromToken(token);
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        // Act
        User result = userService.getUserById(1L);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining(USER_NOT_FOUND);
        verify(userRepository).findById(1L);
    }

    @Test
    void getAuthUserByEmail_ShouldReturnAuthUser_WhenUserExists() {
        // Arrange
        when(authUserRepository.findByEmail("Yousef@email.com")).thenReturn(Optional.of(authUser));
        // Act
        AuthUser result = userService.getAuthUserByEmail("Yousef@email.com");
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("Yousef@email.com");
        verify(authUserRepository).findByEmail("Yousef@email.com");
    }

    @Test
    void getAuthUserByEmail_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(authUserRepository.findByEmail("Yousef@email.com")).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> userService.getAuthUserByEmail("Yousef@email.com"))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining(USER_NOT_FOUND);
        verify(authUserRepository).findByEmail("Yousef@email.com");
    }

    @Test
    void getUserDefaultAddress_ShouldReturnAddress_WhenAddressComplete() {
        // Arrange
        String token = "valid.jwt.token";
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        // Act
        UserAddressDto result = userService.getUserDefaultAddress(httpRequest);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getGovernorate()).isEqualTo("Cairo");
        assertThat(result.getCity()).isEqualTo("Cairo");
        assertThat(result.getStreet()).isEqualTo("Street 1");
        assertThat(result.getApartmentNumber()).isEqualTo("12B");
        verify(httpRequest).getHeader("Authorization");
        verify(jwtService).extractUserIdFromToken(token);
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserDefaultAddress_ShouldThrowException_WhenAddressInComplete() {
        // Arrange
        User userWithoutAddress = User
                .builder()
                .userId(2L)
                .firstName("Joe")
                .lastName("Doe")
                .build();
        String token = "valid.jwt.token";
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUserIdFromToken(token)).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userWithoutAddress));
        // Act & Assert
        assertThatThrownBy(() -> userService.getUserDefaultAddress(httpRequest))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining(DEFAULT_SHIPPING_ADDRESS_NOT_FOUND_FOR_USER);
        verify(httpRequest).getHeader("Authorization");
        verify(jwtService).extractUserIdFromToken(token);
        verify(userRepository).findById(1L);
    }

    @Test
    void validateAuthUserByEmailAndPassword_ShouldNotThrowException_WhenCredentialsValid() {
        // Arrange
        when(authUserRepository.findByEmail("Yousef@email.com")).thenReturn(Optional.of(authUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        // Act & Assert
        assertThatCode(() -> userService.validateAuthUserByEmailAndPassword("Yousef@email.com", "password123"))
                .doesNotThrowAnyException();
        verify(authUserRepository).findByEmail("Yousef@email.com");
        verify(passwordEncoder).matches("password123", "encodedPassword");
    }

    @Test
    void validateAuthUserByEmailAndPassword_ShouldThrowException_WhenEmailNotFound() {
        // Arrange
        when(authUserRepository.findByEmail("notfound@email.com")).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> userService.validateAuthUserByEmailAndPassword("notfound@email.com", "password123"))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining(USER_NOT_FOUND);
        verify(authUserRepository).findByEmail("notfound@email.com");
    }

    @Test
    void validateAuthUserByEmailAndPassword_ShouldThrowException_WhenPasswordInCorrect() {
        // Arrange
        when(authUserRepository.findByEmail("Yousef@email.com")).thenReturn(Optional.of(authUser));
        when(passwordEncoder.matches("wrong-password", "encodedPassword")).thenReturn(false);
        // Act & Assert
        assertThatCode(() -> userService.validateAuthUserByEmailAndPassword("Yousef@email.com", "wrong-password"))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining(USER_NOT_FOUND);
        verify(authUserRepository).findByEmail("Yousef@email.com");
        verify(passwordEncoder).matches("wrong-password", "encodedPassword");
    }

    @Test
    void loginUser_ShouldUpdateLastLogin_WhenValidCredentials() {
        // Arrange
        Instant beforeLogin = Instant.now();
        when(authUserRepository.findByEmail("Yousef@email.com")).thenReturn(Optional.of(authUser));
        when(authUserRepository.save(any(AuthUser.class))).thenAnswer(invocation -> {
            AuthUser savedAuthUser = invocation.getArgument(0);
            assertThat(savedAuthUser.getLastLogin()).isAfterOrEqualTo(beforeLogin);
            return savedAuthUser;
        });
        // Act
        AuthUser result = userService.loginUser(loginRequest);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("Yousef@email.com");
        assertThat(result.getLastLogin()).isAfterOrEqualTo(beforeLogin);
        verify(authUserRepository).findByEmail("Yousef@email.com");
        verify(authUserRepository).save(any(AuthUser.class));
    }

    @Test
    void loginUser_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(authUserRepository.findByEmail("notfound@email.com")).thenReturn(Optional.empty());
        LoginRequest invalidLogin = LoginRequest.builder()
                .email("notfound@email.com")
                .password("password123")
                .build();
        // Act & Assert
        assertThatThrownBy(() -> userService.loginUser(invalidLogin))
                .isInstanceOf(ItemNotFoundException.class);
        verify(authUserRepository).findByEmail("notfound@email.com");
        verify(authUserRepository, never()).save(any(AuthUser.class));
    }
}