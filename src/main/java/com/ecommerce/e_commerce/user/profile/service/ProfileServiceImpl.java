package com.ecommerce.e_commerce.user.profile.service;

import com.ecommerce.e_commerce.security.auth.model.AuthUser;
import com.ecommerce.e_commerce.security.auth.repository.AuthUserRepository;
import com.ecommerce.e_commerce.commerce.order.mapper.OrderMapper;
import com.ecommerce.e_commerce.commerce.order.model.Order;
import com.ecommerce.e_commerce.commerce.order.repository.OrderRepository;
import com.ecommerce.e_commerce.user.profile.model.User;
import com.ecommerce.e_commerce.user.profile.repository.UserRepository;
import com.ecommerce.e_commerce.common.dto.PaginatedResponse;
import com.ecommerce.e_commerce.common.exception.InvalidPasswordException;
import com.ecommerce.e_commerce.user.profile.dto.ChangePasswordRequest;
import com.ecommerce.e_commerce.user.profile.dto.OrderSummeryResponse;
import com.ecommerce.e_commerce.user.profile.dto.ProfileResponse;
import com.ecommerce.e_commerce.user.profile.dto.UpdateProfileRequest;
import com.ecommerce.e_commerce.user.profile.mapper.ProfileMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ecommerce.e_commerce.common.utils.Constants.CURRENT_PASSWORD_IS_INCORRECT;
import static com.ecommerce.e_commerce.common.utils.Constants.NEW_PASSWORD_MUST_BE_DIFFERENT_FROM_CURRENT_PASSWORD;

@RequiredArgsConstructor
@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthUserRepository authUserRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile(HttpServletRequest request) {
        User user = userService.getUserByRequest(request);
        return profileMapper.toResponse(user);
    }

    @Override
    @Transactional
    public ProfileResponse updateProfile(HttpServletRequest request, UpdateProfileRequest profileRequest) {
        User user = userService.getUserByRequest(request);
        profileMapper.updateUserFromRequest(profileRequest, user);
        User updatedUser = userRepository.save(user);
        return profileMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public void changePassword(HttpServletRequest request, ChangePasswordRequest passwordRequest) {
        User user = userService.getUserByRequest(request);
        AuthUser authUser = user.getAuthUser();

        if (!passwordEncoder.matches(passwordRequest.getOldPassword(), authUser.getPassword())) {
            throw new InvalidPasswordException(CURRENT_PASSWORD_IS_INCORRECT);
        }
        if (passwordRequest.getNewPassword().equals(passwordRequest.getOldPassword())) {
            throw new InvalidPasswordException(NEW_PASSWORD_MUST_BE_DIFFERENT_FROM_CURRENT_PASSWORD);
        }
        authUser.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        authUserRepository.save(authUser);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<OrderSummeryResponse> getMyOrders(HttpServletRequest request, Pageable pageable) {
        User user = userService.getUserByRequest(request);
        Page<Order> orderPage = orderRepository.findByUser_UserIdOrderByCreatedAtDesc(user.getUserId(), pageable);
        List<OrderSummeryResponse>content=orderPage.map(orderMapper::toSummaryResponse).getContent();
        return new PaginatedResponse<>(content,orderPage.getTotalElements());
    }
}
