package com.ecommerce.e_commerce.security.auth.service;

import com.ecommerce.e_commerce.security.auth.model.AuthUser;
import com.ecommerce.e_commerce.security.auth.repository.AuthUserRepository;
import com.ecommerce.e_commerce.security.auth.utils.JwtUtils;
import com.ecommerce.e_commerce.common.exception.ItemNotFoundException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.ecommerce.e_commerce.common.utils.Constants.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final AuthUserRepository authUserRepository;

    @Override
    public String extractUsername(String token) {
        return JwtUtils.extractClaim(token, Claims::getSubject);
    }

    @Override
    public Long extractUserIdFromToken(String token) {
        String userEmail = extractUsername(token);
        AuthUser authUser = authUserRepository.findByEmail(userEmail).orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));
        return authUser.getUserId();
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return JwtUtils.extractClaim(token, Claims::getExpiration);
    }


}
