package com.ecommerce.e_commerce.user.profile.service;

import com.ecommerce.e_commerce.commerce.order.mapper.OrderMapper;
import com.ecommerce.e_commerce.commerce.order.model.Order;
import com.ecommerce.e_commerce.commerce.order.repository.OrderRepository;
import com.ecommerce.e_commerce.commerce.payment.enums.PaymentMethod;
import com.ecommerce.e_commerce.common.dto.PaginatedResponse;
import com.ecommerce.e_commerce.common.exception.InvalidPasswordException;
import com.ecommerce.e_commerce.common.exception.ItemNotFoundException;
import com.ecommerce.e_commerce.security.auth.model.AuthUser;
import com.ecommerce.e_commerce.security.auth.repository.AuthUserRepository;
import com.ecommerce.e_commerce.user.profile.dto.ChangePasswordRequest;
import com.ecommerce.e_commerce.user.profile.dto.OrderSummeryResponse;
import com.ecommerce.e_commerce.user.profile.dto.ProfileResponse;
import com.ecommerce.e_commerce.user.profile.dto.UpdateProfileRequest;
import com.ecommerce.e_commerce.user.profile.mapper.ProfileMapper;
import com.ecommerce.e_commerce.user.profile.model.User;
import com.ecommerce.e_commerce.user.profile.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.List;

import static com.ecommerce.e_commerce.common.utils.Constants.USER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static com.ecommerce.e_commerce.common.utils.Constants.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProfileMapper profileMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthUserRepository authUserRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private HttpServletRequest httpRequest;
    @InjectMocks
    private ProfileServiceImpl profileService;

    private User user;
    private AuthUser authUser;
    private ProfileResponse profileResponse;
    private UpdateProfileRequest updateProfileRequest;
    private ChangePasswordRequest changePasswordRequest;


    @BeforeEach
    void setUp() {
        authUser = AuthUser
                .builder()
                .authUserId(1L)
                .password("encodedPassword")
                .email("john.doe@example.com")
                .build();

        user = User
                .builder()
                .userId(1L)
                .firstName("John")
                .lastName("Doe")
                .authUser(authUser)
                .build();

        profileResponse = ProfileResponse
                .builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .phoneCode(1)
                .hasCompleteAddress(true)
                .build();

        updateProfileRequest = UpdateProfileRequest
                .builder()
                .firstName("Ahmed")
                .lastName("Mohamed")
                .phone("9876543210")
                .phoneCode(20)
                .governorate("Cairo")
                .city("Cairo")
                .street("123 Main St")
                .floorNumber("5")
                .apartmentNumber("10")
                .build();

        changePasswordRequest = ChangePasswordRequest
                .builder()
                .oldPassword("oldPassword")
                .newPassword("newPassword123")
                .confirmPassword("newPassword123")
                .build();
    }

    @Test
    void getMyProfile_ShouldReturnProfileResponse_WhenUserExists() {
        // Arrange
        when(userService.getUserByRequest(httpRequest)).thenReturn(user);
        when(profileMapper.toResponse(user)).thenReturn(profileResponse);
        // Act
        ProfileResponse result = profileService.getMyProfile(httpRequest);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(userService).getUserByRequest(httpRequest);
        verify(profileMapper).toResponse(user);
    }

    @Test
    void getMyProfile_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userService.getUserByRequest(httpRequest))
                .thenThrow(new ItemNotFoundException(USER_NOT_FOUND));
        // Act & Assert
        assertThatThrownBy(() -> profileService.getMyProfile(httpRequest))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining(USER_NOT_FOUND);
        verify(userService).getUserByRequest(httpRequest);
    }

    @Test
    void updateProfile_ShouldReturnUpdatedProfile_WhenValidRequest() {
        // Arrange
        when(userService.getUserByRequest(httpRequest)).thenReturn(user);
        doAnswer(invocation -> {
            UpdateProfileRequest request = invocation.getArgument(0);
            User user = invocation.getArgument(1);
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPhone(request.getPhone());
            user.setPhoneCode(request.getPhoneCode());
            return null;
        }).when(profileMapper).updateUserFromRequest(any(), any());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(profileMapper.toResponse(any(User.class)))
                .thenAnswer(invocation -> {
                    User u = invocation.getArgument(0);
                    return ProfileResponse
                            .builder()
                            .firstName(u.getFirstName())
                            .lastName(u.getLastName())
                            .phone(u.getPhone())
                            .phoneCode(u.getPhoneCode())
                            .build();
                });
        // Act
        ProfileResponse result = profileService.updateProfile(httpRequest, updateProfileRequest);
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Ahmed");
        assertThat(result.getLastName()).isEqualTo("Mohamed");
        verify(userService).getUserByRequest(httpRequest);
        verify(profileMapper).updateUserFromRequest(updateProfileRequest, user);
        verify(userRepository).save(any(User.class));
        verify(profileMapper).toResponse(any(User.class));
    }

    @Test
    void changePassword_ShouldUpdatePassword_WhenValidRequest() {
        // Arrange
        when(userService.getUserByRequest(httpRequest)).thenReturn(user);
        when(passwordEncoder.matches("oldPassword", authUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        when(authUserRepository.save(any(AuthUser.class))).thenReturn(authUser);
        // Act
        profileService.changePassword(httpRequest, changePasswordRequest);
        // Assert
        assertThat(authUser.getPassword()).isEqualTo("encodedNewPassword");
        verify(userService).getUserByRequest(httpRequest);
        verify(passwordEncoder).matches("oldPassword", "encodedPassword");
        verify(passwordEncoder).encode("newPassword123");
        verify(authUserRepository).save(any(AuthUser.class));
    }

    @Test
    void changePassword_ShouldThrowException_WhenOldPasswordIsIncorrect() {
        // Arrange
        when(userService.getUserByRequest(httpRequest)).thenReturn(user);
        when(passwordEncoder.matches("oldPassword", authUser.getPassword())).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> profileService.changePassword(httpRequest, changePasswordRequest))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining(CURRENT_PASSWORD_IS_INCORRECT);
        verify(userService).getUserByRequest(httpRequest);
        verify(passwordEncoder).matches("oldPassword", "encodedPassword");
        verify(passwordEncoder, never()).encode(any());
        verify(authUserRepository, never()).save(any());
    }

    @Test
    void changePassword_ShouldThrowException_WhenNewPasswordSameAsOld() {
        // Arrange
        ChangePasswordRequest changePasswordRequest2 = ChangePasswordRequest
                .builder()
                .oldPassword("samePassword")
                .newPassword("samePassword")
                .confirmPassword("samePassword")
                .build();
        when(userService.getUserByRequest(httpRequest)).thenReturn(user);
        when(passwordEncoder.matches("samePassword", authUser.getPassword())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> profileService.changePassword(httpRequest, changePasswordRequest2))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining(NEW_PASSWORD_MUST_BE_DIFFERENT_FROM_CURRENT_PASSWORD);
        verify(userService).getUserByRequest(httpRequest);
        verify(passwordEncoder).matches("samePassword", "encodedPassword");
        verify(passwordEncoder, never()).encode(any());
        verify(authUserRepository, never()).save(any());
    }

    @Test
    void getMyOrders_ShouldReturnPaginatedOrders_WhenOrdersExist() {
        // Arrange
        Order order1 = Order
                .builder()
                .orderId(1L)
                .user(user)
                .build();
        Order order2 = Order
                .builder()
                .orderId(2L)
                .user(user)
                .build();
        OrderSummeryResponse orderSummeryResponse1 = OrderSummeryResponse
                .builder()
                .orderId(1L)
                .paymentMethod(PaymentMethod.COD)
                .build();
        OrderSummeryResponse orderSummeryResponse2 = OrderSummeryResponse
                .builder()
                .orderId(2L)
                .paymentMethod(PaymentMethod.PAYPAL)
                .build();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(List.of(order1, order2), pageable, 2);

        when(userService.getUserByRequest(httpRequest)).thenReturn(user);
        when(orderRepository.findByUser_UserIdOrderByCreatedAtDesc(user.getUserId(), pageable)).thenReturn(orderPage);
        when(orderMapper.toSummaryResponse(order1)).thenReturn(orderSummeryResponse1);
        when(orderMapper.toSummaryResponse(order2)).thenReturn(orderSummeryResponse2);
        // Act
        PaginatedResponse<OrderSummeryResponse> result = profileService.getMyOrders(httpRequest, pageable);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPayload()).hasSize(2);
        assertThat(result.getTotal()).isEqualTo(2);
        assertThat(result.getPayload().get(0).getPaymentMethod()).isEqualTo(PaymentMethod.COD);
        assertThat(result.getPayload().get(1).getPaymentMethod()).isEqualTo(PaymentMethod.PAYPAL);
        verify(userService).getUserByRequest(httpRequest);
        verify(orderRepository).findByUser_UserIdOrderByCreatedAtDesc(user.getUserId(), pageable);
        verify(orderMapper, times(2)).toSummaryResponse(any(Order.class));
    }

    @Test
    void getMyOrders_ShouldReturnEmptyList_WhenNoOrdersExist() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(userService.getUserByRequest(httpRequest)).thenReturn(user);
        when(orderRepository.findByUser_UserIdOrderByCreatedAtDesc(user.getUserId(), pageable)).thenReturn(emptyPage);
        // Act
        PaginatedResponse<OrderSummeryResponse> result = profileService.getMyOrders(httpRequest, pageable);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPayload()).isEmpty();
        assertThat(result.getTotal()).isEqualTo(0L);
        verify(userService).getUserByRequest(httpRequest);
        verify(orderRepository).findByUser_UserIdOrderByCreatedAtDesc(user.getUserId(), pageable);
        verify(orderMapper, never()).toSummaryResponse(any(Order.class));
    }
}