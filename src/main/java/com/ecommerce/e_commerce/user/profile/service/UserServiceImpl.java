package com.ecommerce.e_commerce.user.profile.service;

import com.ecommerce.e_commerce.security.auth.dto.CustomerRegistrationRequest;
import com.ecommerce.e_commerce.security.auth.dto.LoginRequest;
import com.ecommerce.e_commerce.security.auth.dto.RegisterRequest;
import com.ecommerce.e_commerce.security.auth.dto.SellerRegistrationRequest;
import com.ecommerce.e_commerce.security.auth.model.AuthUser;
import com.ecommerce.e_commerce.security.auth.repository.AuthUserRepository;
import com.ecommerce.e_commerce.security.auth.repository.RoleRepository;
import com.ecommerce.e_commerce.security.auth.service.EmailVerificationService;
import com.ecommerce.e_commerce.security.auth.service.JwtService;
import com.ecommerce.e_commerce.security.auth.utils.JwtUtils;
import com.ecommerce.e_commerce.common.exception.DuplicateItemException;
import com.ecommerce.e_commerce.common.exception.ItemNotFoundException;
import com.ecommerce.e_commerce.security.auth.model.Role;
import com.ecommerce.e_commerce.common.exception.UnauthorizedException;
import com.ecommerce.e_commerce.commerce.order.dto.UserAddressDto;
import com.ecommerce.e_commerce.security.auth.enums.RoleEnum;
import com.ecommerce.e_commerce.user.profile.model.User;
import com.ecommerce.e_commerce.user.profile.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;

import static com.ecommerce.e_commerce.common.utils.Constants.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final AuthUserRepository authUserRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailVerificationService emailVerificationService;
    private final JwtService jwtService;

    @Override
    public AuthUser createUser(RegisterRequest registerRequest) {
        validateEmailVerification(registerRequest.getVerifiedEmail());
        ValidateDuplicateUser(registerRequest.getVerifiedEmail());
        return saveUser(registerRequest);
    }

    @Override
    public Long getUserId(HttpServletRequest request) {
        String token = JwtUtils.extractTokenFromHeader(request);
        return jwtService.extractUserIdFromToken(token);
    }

    @Override
    public User getUserByRequest(HttpServletRequest request) {
        Long userId = getUserId(request);
        return getUserById(userId);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));
    }

    @Override
    public AuthUser getAuthUserByEmail(String email) {
        return authUserRepository.findByEmail(email)
                .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));
    }

    @Override
    public UserAddressDto getUserDefaultAddress(HttpServletRequest request) {
        User user = getUserByRequest(request);
        if (!user.hasCompleteShippingAddress()) {
            throw new ItemNotFoundException(DEFAULT_SHIPPING_ADDRESS_NOT_FOUND_FOR_USER);
        }
        return buildUserAddressDtoFromUser(user);
    }

    @Override
    public void validateAuthUserByEmailAndPassword(String email, String password) {
        AuthUser authUser = authUserRepository.findByEmail(email)
                .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));
        if (!passwordEncoder.matches(password, authUser.getPassword())) {
            throw new ItemNotFoundException(USER_NOT_FOUND);
        }
    }

    private void validateEmailVerification(String email) {
        boolean isVerified = emailVerificationService.isEmailVerified(email);
        if (!isVerified) {
            throw new UnauthorizedException(EMAIL_IS_NOT_VERIFIED);
        }
    }

    private void ValidateDuplicateUser(String email) {
        if (authUserRepository.findByEmail(email).isPresent()) {
            throw new DuplicateItemException(EMAIL_ALREADY_EXISTS);
        }
    }

    public AuthUser saveUser(RegisterRequest registerRequest) {
        User user = saveUserDetails(registerRequest);
        return saveAuthUserDetails(registerRequest, user);
    }

    private User saveUserDetails(RegisterRequest registerRequest) {
        User user = createUserObject(registerRequest);
        return userRepository.save(user);
    }

    private AuthUser saveAuthUserDetails(RegisterRequest registerRequest, User user) {
        AuthUser authUser = createAuthUserObject(registerRequest, user);
        return authUserRepository.save(authUser);
    }

    private AuthUser createAuthUserObject(RegisterRequest registerRequest, User user) {
        Role selectedRole = getRoleByEnum(registerRequest.getRole());
        return AuthUser.builder()
                .email(registerRequest.getVerifiedEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .lastLogin(Instant.now())
                .roles(Collections.singleton(selectedRole))
                .user(user)
                .build();
    }

    private User createUserObject(RegisterRequest registerRequest) {
        Role role = getRoleByEnum(registerRequest.getRole());
        if (role.getRoleEnum() == RoleEnum.CUSTOMER) {
            CustomerRegistrationRequest customer = registerRequest.getCustomer();
            return buildCustomerUser(customer);
        }
        return buildSellerUser(registerRequest.getSeller());
    }

    @Override
    public AuthUser loginUser(LoginRequest loginRequest) {
        AuthUser authUser = getUserByEmail(loginRequest.getEmail());
        updateLastLogin(authUser);
        return authUser;
    }

    private void updateLastLogin(AuthUser authUser) {
        authUser.setLastLogin(Instant.now());
        authUserRepository.save(authUser);
    }

    private Role getRoleByEnum(RoleEnum roleEnum) {
        return roleRepository
                .findByRoleEnum(roleEnum)
                .orElseThrow(() -> new ItemNotFoundException("Role not found"));
    }

    private User buildCustomerUser(CustomerRegistrationRequest customer) {
        return User.builder()
                .firstName(customer.getFirstname())
                .lastName(customer.getLastname())
                .phoneCode(customer.getPhoneCode())
                .phone(customer.getPhone())
                .build();
    }

    private User buildSellerUser(SellerRegistrationRequest seller) {
        return User.builder()
                .shippingZone(seller.getShippingZone())
                .brandName(seller.getBrandName())
                .businessAddress(seller.getBusinessAddress())
                .sellerType(seller.getSellerType())
                .build();
    }

    private AuthUser getUserByEmail(String email) {
        return authUserRepository.findByEmail(email).orElseThrow(() -> new ItemNotFoundException(EMAIL_NOT_FOUND));
    }

    private UserAddressDto buildUserAddressDtoFromUser(User user) {
        return UserAddressDto.builder()
                .governorate(user.getGovernorate())
                .city(user.getCity())
                .street(user.getStreet())
                .floorNumber(user.getFloorNumber())
                .apartmentNumber(user.getApartmentNumber())
                .phone(user.getPhone())
                .build();
    }
}
