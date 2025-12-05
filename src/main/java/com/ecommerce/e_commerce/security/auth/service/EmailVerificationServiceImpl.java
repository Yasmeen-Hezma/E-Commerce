package com.ecommerce.e_commerce.security.auth.service;

import com.ecommerce.e_commerce.security.auth.enums.OtpPurpose;
import com.ecommerce.e_commerce.security.auth.model.EmailVerification;
import com.ecommerce.e_commerce.security.auth.repository.AuthUserRepository;
import com.ecommerce.e_commerce.security.auth.repository.EmailVerificationRepository;
import com.ecommerce.e_commerce.common.exception.InvalidOtpException;
import com.ecommerce.e_commerce.common.exception.ItemNotFoundException;
import com.ecommerce.e_commerce.common.exception.UnauthorizedException;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.ecommerce.e_commerce.common.utils.Constants.*;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {
    public static final int OTP_EXPIRY_MINUTES = 10;
    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailService emailService;
    private final AuthUserRepository authUserRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public void sendOtp(String email) throws MessagingException {
        String otp = generateOtp();

        emailVerificationRepository.deleteByEmailAndPurpose(email, OtpPurpose.EMAIL_VERIFICATION);

        EmailVerification verification = buildEmailVerification(email, otp, OtpPurpose.EMAIL_VERIFICATION);

        emailVerificationRepository.save(verification);
        emailService.sendOtpEmail(email, otp);
    }

    @Override
    @Transactional
    public void verifyOtp(String email, String otp) {
        EmailVerification verification = getVerificationByEmailAndPurpose(email, OtpPurpose.EMAIL_VERIFICATION);
        validateOtp(verification, otp);
        verification.setVerified(true);
        emailVerificationRepository.save(verification);
    }

    @Override
    @Transactional
    public void sendPasswordResetOtp(String email) throws MessagingException {
        validateUserExists(email);

        String otp = generateOtp();
        emailVerificationRepository.deleteByEmailAndPurpose(email, OtpPurpose.PASSWORD_RESET);

        EmailVerification verification = buildEmailVerification(email, otp, OtpPurpose.PASSWORD_RESET);

        emailVerificationRepository.save(verification);
        emailService.sendPasswordResetEmail(email, otp);
    }

    @Override
    @Transactional
    public void verifyPasswordResetOtp(String email, String otp) {
        EmailVerification verification = getVerificationByEmailAndPurpose(email, OtpPurpose.PASSWORD_RESET);
        validateOtp(verification, otp);
        verification.setVerified(true);
        emailVerificationRepository.save(verification);
    }

    @Override
    public boolean isEmailVerified(String email) {
        return emailVerificationRepository
                .findByEmailAndPurpose(email, OtpPurpose.EMAIL_VERIFICATION)
                .map(EmailVerification::isVerified)
                .orElse(false);
    }

    @Override
    @Transactional
    public void removePasswordResetOtp(String email) {
        emailVerificationRepository.deleteByEmailAndPurpose(email, OtpPurpose.PASSWORD_RESET);
    }

    private void validateOtp(EmailVerification verification, String otp) {
        if (verification.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidOtpException(OTP_EXPIRED);
        }
        if (!verification.getOtp().equals(otp)) {
            throw new InvalidOtpException(INVALID_OTP);
        }
    }


    private EmailVerification getVerificationByEmailAndPurpose(String email, OtpPurpose purpose) {
        return emailVerificationRepository
                .findByEmailAndPurpose(email, purpose).orElseThrow(() -> new UnauthorizedException(OTP_NOT_FOUND_FOR_THIS_EMAIL));
    }

    private String generateOtp() {
        // Create (6-digit OTP)
        int otp = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(otp);
    }

    private void validateUserExists(String email) {
        authUserRepository.findByEmail(email)
                .orElseThrow(() -> new ItemNotFoundException(NO_USER_FOUND_WITH_THIS_EMAIL));
    }

    private EmailVerification buildEmailVerification(String email, String otp, OtpPurpose purpose) {
        return EmailVerification.builder()
                .email(email)
                .otp(otp)
                .purpose(purpose)
                .expiresAt(Instant.now().plus(OTP_EXPIRY_MINUTES, ChronoUnit.MINUTES))
                .verified(false)
                .build();
    }
}
