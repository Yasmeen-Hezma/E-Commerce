package com.ecommerce.e_commerce.user.profile.service;

import com.ecommerce.e_commerce.common.dto.PaginatedResponse;
import com.ecommerce.e_commerce.user.profile.dtos.ChangePasswordRequest;
import com.ecommerce.e_commerce.user.profile.dtos.OrderSummeryResponse;
import com.ecommerce.e_commerce.user.profile.dtos.ProfileResponse;
import com.ecommerce.e_commerce.user.profile.dtos.UpdateProfileRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;

public interface ProfileService {
    ProfileResponse getMyProfile(HttpServletRequest request);

    ProfileResponse updateProfile(HttpServletRequest request, UpdateProfileRequest profileRequest);

    void changePassword(HttpServletRequest request, ChangePasswordRequest passwordRequest);

    PaginatedResponse<OrderSummeryResponse> getMyOrders(HttpServletRequest request, Pageable pageable);

}
