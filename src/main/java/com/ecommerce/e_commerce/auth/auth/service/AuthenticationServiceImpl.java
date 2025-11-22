package com.ecommerce.e_commerce.auth.auth.service;

import com.ecommerce.e_commerce.auth.auth.dto.EmailRequest;
import com.ecommerce.e_commerce.auth.auth.dto.LoginRequest;
import com.ecommerce.e_commerce.auth.auth.dto.RegisterRequest;
import com.ecommerce.e_commerce.auth.auth.dto.ResetPasswordRequest;
import com.ecommerce.e_commerce.auth.auth.model.AuthUser;
import com.ecommerce.e_commerce.auth.auth.repository.AuthUserRepository;
import com.ecommerce.e_commerce.auth.auth.utils.JwtUtils;
import com.ecommerce.e_commerce.auth.token.dto.TokenResponse;
import com.ecommerce.e_commerce.auth.token.service.TokenService;
import com.ecommerce.e_commerce.core.common.exception.ItemNotFoundException;
import com.ecommerce.e_commerce.core.common.exception.UnauthorizedException;
import com.ecommerce.e_commerce.core.user.enums.RoleEnum;
import com.ecommerce.e_commerce.core.user.service.UserServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.beans.Transient;

import static com.ecommerce.e_commerce.core.common.utils.Constants.*;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserServiceImpl userService;
    private final TokenService tokenService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuthUserRepository authUserRepository;
    private final EmailVerificationService emailVerificationService;
    private final PasswordEncoder passwordEncoder;


    @Override
    public TokenResponse register(RegisterRequest registerRequest) {
        AuthUser user = userService.createUser(registerRequest);
        return generateAuthenticationToken(user);
    }

    @Override
    public TokenResponse login(LoginRequest loginRequest) {
        userService.validateAuthUserByEmailAndPassword(loginRequest.getEmail(), loginRequest.getPassword());
        authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
        AuthUser user = userService.loginUser(loginRequest);
        return generateAuthenticationToken(user);
    }

    @Override
    public TokenResponse refreshToken(HttpServletRequest request) {
        String refreshToken = JwtUtils.extractTokenFromHeader(request);
        String userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail == null) {
            throw new UnauthorizedException(USER_NOT_FOUND);
        }

        AuthUser user = authUserRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ItemNotFoundException(EMAIL_NOT_FOUND));

        if (!jwtService.isTokenValid(refreshToken, user) && tokenService.isTokenValidInDB(refreshToken)) {
            throw new UnauthorizedException(REFRESH_TOKEN_IS_INVALID_OR_EXPIRED);
        }
        return tokenService.generateNewAccessToken(user, refreshToken);
    }

    @Override
    @Transactional
    public void initiatePasswordReset(EmailRequest request) throws MessagingException {
        emailVerificationService.sendPasswordResetOtp(request.getEmail());
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String email = request.getEmail();
        String otp = request.getOtp();
        String newPassword = request.getNewPassword();

        emailVerificationService.verifyPasswordResetOtp(email, otp);

        AuthUser user = userService.getAuthUserByEmail(email);

        user.setPassword(passwordEncoder.encode(newPassword));
        authUserRepository.save(user);
        // force re-login
        tokenService.revokeAllUserTokens(user);

        emailVerificationService.removePasswordResetOtp(email);
    }

    private void authenticateUser(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }

    private TokenResponse generateAuthenticationToken(AuthUser user) {
        RoleEnum roleId = user.getRoles().iterator().next().getRoleEnum();
        String accessToken = tokenService.generateAccessToken(user.getEmail(), roleId);
        String refreshToken = tokenService.generateRefreshToken(user.getEmail(), roleId);
        tokenService.revokeAllUserTokens(user);
        tokenService.saveToken(accessToken, user);
        return new TokenResponse(accessToken, refreshToken);
    }
}
