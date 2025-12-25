package com.ecommerce.e_commerce.user.profile.service;

import com.ecommerce.e_commerce.common.dto.PaginatedResponse;
import com.ecommerce.e_commerce.user.profile.dto.ChangePasswordRequest;
import com.ecommerce.e_commerce.user.profile.dto.OrderSummeryResponse;
import com.ecommerce.e_commerce.user.profile.dto.ProfileResponse;
import com.ecommerce.e_commerce.user.profile.dto.UpdateProfileRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;

public interface ProfileService {
    ProfileResponse getMyProfile(HttpServletRequest request);

    ProfileResponse updateProfile(HttpServletRequest request, UpdateProfileRequest profileRequest);

    void changePassword(HttpServletRequest request, ChangePasswordRequest passwordRequest);

    PaginatedResponse<OrderSummeryResponse> getMyOrders(HttpServletRequest request, Pageable pageable);

}
