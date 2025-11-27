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
import com.ecommerce.e_commerce.user.profile.dtos.ChangePasswordRequest;
import com.ecommerce.e_commerce.user.profile.dtos.OrderSummeryResponse;
import com.ecommerce.e_commerce.user.profile.dtos.ProfileResponse;
import com.ecommerce.e_commerce.user.profile.dtos.UpdateProfileRequest;
import com.ecommerce.e_commerce.user.profile.mapper.ProfileMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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
            throw new InvalidPasswordException("Current password is incorrect");
        }
        if (passwordRequest.getNewPassword().equals(passwordRequest.getOldPassword())) {
            throw new InvalidPasswordException("New password must be different from current password");
        }
        authUser.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
        authUserRepository.save(authUser);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<OrderSummeryResponse> getMyOrders(HttpServletRequest request, Pageable pageable) {
        User user = userService.getUserByRequest(request);
        Page<Order> orderPage = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
        List<OrderSummeryResponse>content=orderPage.map(orderMapper::toSummaryResponse).getContent();
        return new PaginatedResponse<>(content,orderPage.getTotalElements());
    }
}
