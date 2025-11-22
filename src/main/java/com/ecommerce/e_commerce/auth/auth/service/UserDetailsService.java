package com.ecommerce.e_commerce.auth.auth.service;

import com.ecommerce.e_commerce.auth.auth.repository.AuthUserRepository;
import com.ecommerce.e_commerce.core.common.exception.ItemNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.ecommerce.e_commerce.core.common.utils.Constants.USER_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final AuthUserRepository authUserRepository;

    @Override
    public UserDetails loadUserByUsername(
            String email
    ) throws UsernameNotFoundException {
        return authUserRepository.findByEmail(email)
                .orElseThrow(() -> new ItemNotFoundException(USER_NOT_FOUND));
    }
}
