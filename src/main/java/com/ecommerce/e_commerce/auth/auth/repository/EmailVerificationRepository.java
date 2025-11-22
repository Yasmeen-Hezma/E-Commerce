package com.ecommerce.e_commerce.auth.auth.repository;

import com.ecommerce.e_commerce.auth.auth.enums.OtpPurpose;
import com.ecommerce.e_commerce.auth.auth.model.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByEmailAndPurpose(String email, OtpPurpose purpose);

    void deleteByEmailAndPurpose(String email, OtpPurpose purpose);
}
