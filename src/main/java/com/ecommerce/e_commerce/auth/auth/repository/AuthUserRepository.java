package com.ecommerce.e_commerce.auth.auth.repository;

import com.ecommerce.e_commerce.auth.auth.model.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<AuthUser, Integer> {
    Optional<AuthUser> findByEmail(String email);

    Optional<AuthUser> findByEmailAndPassword(String email, String password);

}