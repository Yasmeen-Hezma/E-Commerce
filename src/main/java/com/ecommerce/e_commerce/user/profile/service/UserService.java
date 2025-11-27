package com.ecommerce.e_commerce.user.profile.service;

import com.ecommerce.e_commerce.security.auth.dto.LoginRequest;
import com.ecommerce.e_commerce.security.auth.dto.RegisterRequest;
import com.ecommerce.e_commerce.security.auth.model.AuthUser;
import com.ecommerce.e_commerce.commerce.order.dto.UserAddressDto;
import com.ecommerce.e_commerce.user.profile.model.User;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    AuthUser createUser(RegisterRequest registerRequest);

    AuthUser loginUser(LoginRequest loginRequest);

    User getUserByRequest(HttpServletRequest request);

    Long getUserId(HttpServletRequest request);

    User getUserById(Long userId);

    AuthUser getAuthUserByEmail(String email);

    UserAddressDto getUserDefaultAddress(HttpServletRequest request);

    void validateAuthUserByEmailAndPassword(String email, String password);
}
