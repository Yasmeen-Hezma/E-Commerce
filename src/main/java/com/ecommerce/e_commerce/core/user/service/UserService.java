package com.ecommerce.e_commerce.core.user.service;

import com.ecommerce.e_commerce.auth.auth.model.AuthUser;
import com.ecommerce.e_commerce.core.order.dto.UserAddressDto;
import com.ecommerce.e_commerce.core.user.model.User;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    User getUserByRequest(HttpServletRequest request);

    Long getUserId(HttpServletRequest request);

    User getUserById(Long userId);

    AuthUser getAuthUserByEmail(String email);

    UserAddressDto getUserDefaultAddress(HttpServletRequest request);

    boolean hasDefaultAddress(HttpServletRequest request);

    void validateAuthUserByEmailAndPassword(String email, String password);

}
