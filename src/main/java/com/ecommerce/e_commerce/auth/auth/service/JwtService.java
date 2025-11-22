package com.ecommerce.e_commerce.auth.auth.service;

import com.ecommerce.e_commerce.auth.auth.model.AuthUser;
import com.ecommerce.e_commerce.auth.auth.repository.AuthUserRepository;
import com.ecommerce.e_commerce.auth.auth.utils.JwtUtils;
import com.ecommerce.e_commerce.core.common.exception.ItemNotFoundException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.ecommerce.e_commerce.core.common.utils.Constants.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final AuthUserRepository authUserRepository;

    public String extractUsername(String token) {
        return JwtUtils.extractClaim(token, Claims::getSubject);
    }

    public Long extractUserIdFromToken(String token) {
        String userEmail = extractUsername(token);
        AuthUser authUser = authUserRepository.findByEmail(userEmail).orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));
        return authUser.getId();
    }

    public Date extractExpiration(String token) {
        return JwtUtils.extractClaim(token, Claims::getExpiration);
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

}
