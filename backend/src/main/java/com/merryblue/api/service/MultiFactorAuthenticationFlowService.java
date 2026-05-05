package com.merryblue.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles the complex state machine of Multi-Factor Authentication (MFA).
 * Manages OTP generation, delivery routing (SMS vs Email vs App), verification,
 * rate limiting, lockout mechanisms, and backup code generation/validation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MultiFactorAuthenticationFlowService {

    private final EmailService emailService;
    private final SmsService smsService;

    // In-memory store for active MFA challenges. In production, use Redis.
    private final Map<UUID, MfaChallenge> activeChallenges = new ConcurrentHashMap<>();
    
    // In-memory store for lockouts. Use Redis in prod.
    private final Map<UUID, LockoutState> userLockouts = new ConcurrentHashMap<>();

    private static final int MAX_ATTEMPTS = 5;
    private static final int OTP_VALIDITY_MINUTES = 10;
    private static final int LOCKOUT_DURATION_MINUTES = 30;

    /**
     * Initiates an MFA challenge for a user, generating an OTP and sending it via the preferred channel.
     */
    public MfaInitiationResult initiateMfaChallenge(UUID userId, String preferredChannel, String destination) {
        log.info("Initiating MFA Challenge for user {} via {}", userId, preferredChannel);

        if (isUserLockedOut(userId)) {
            log.warn("User {} is currently locked out of MFA.", userId);
            return new MfaInitiationResult(false, null, "Account is temporarily locked due to too many failed attempts.");
        }

        // Generate 6-digit OTP
        String otp = generateNumericOtp(6);
        UUID challengeId = UUID.randomUUID();

        MfaChallenge challenge = new MfaChallenge(
                userId, otp, OffsetDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES), preferredChannel
        );
        
        activeChallenges.put(challengeId, challenge);

        // Dispatch based on channel
        boolean dispatchSuccess = dispatchOtp(preferredChannel, destination, otp);

        if (!dispatchSuccess) {
            activeChallenges.remove(challengeId);
            return new MfaInitiationResult(false, null, "Failed to deliver OTP via " + preferredChannel);
        }

        return new MfaInitiationResult(true, challengeId, "OTP sent successfully via " + preferredChannel);
    }

    /**
     * Verifies an OTP provided by the user against an active challenge.
     * Handles attempt counting and lockouts.
     */
    @Transactional
    public MfaVerificationResult verifyMfaChallenge(UUID challengeId, String providedOtp) {
        log.info("Verifying MFA Challenge {}", challengeId);

        MfaChallenge challenge = activeChallenges.get(challengeId);
        if (challenge == null) {
            return new MfaVerificationResult(false, "Invalid or expired challenge.");
        }

        UUID userId = challenge.userId;

        if (isUserLockedOut(userId)) {
            activeChallenges.remove(challengeId);
            return new MfaVerificationResult(false, "Account is temporarily locked.");
        }

        if (OffsetDateTime.now().isAfter(challenge.expiresAt)) {
            activeChallenges.remove(challengeId);
            return new MfaVerificationResult(false, "OTP has expired.");
        }

        if (challenge.otp.equals(providedOtp)) {
            // Success!
            log.info("MFA Challenge {} successful for user {}", challengeId, userId);
            activeChallenges.remove(challengeId);
            resetLockout(userId);
            // In a real app, generate the final JWT here
            return new MfaVerificationResult(true, "Authentication successful.");
        } else {
            // Failure
            log.warn("MFA Challenge {} failed for user {}", challengeId, userId);
            recordFailedAttempt(userId);
            
            LockoutState state = userLockouts.get(userId);
            int attemptsLeft = MAX_ATTEMPTS - (state != null ? state.failedAttempts : 1);
            
            if (attemptsLeft <= 0) {
                activeChallenges.remove(challengeId);
                return new MfaVerificationResult(false, "Maximum attempts exceeded. Account locked for " + LOCKOUT_DURATION_MINUTES + " minutes.");
            }
            
            return new MfaVerificationResult(false, "Invalid OTP. " + attemptsLeft + " attempts remaining.");
        }
    }

    /**
     * Verifies a static backup code (used when user loses access to phone/email).
     */
    @Transactional
    public MfaVerificationResult verifyBackupCode(UUID userId, String backupCode) {
        log.info("Verifying MFA Backup Code for user {}", userId);
        
        if (isUserLockedOut(userId)) {
            return new MfaVerificationResult(false, "Account is temporarily locked.");
        }

        // Logic: Query DB for backup code, check if valid and unused.
        // Mocking the DB check:
        boolean isValidBackupCode = "12345678-abcd-1234".equals(backupCode); // Mock DB check

        if (isValidBackupCode) {
            log.info("Backup code accepted for user {}", userId);
            // In a real app, MARK THIS BACKUP CODE AS USED in the DB
            resetLockout(userId);
            return new MfaVerificationResult(true, "Authentication via backup code successful.");
        } else {
            log.warn("Invalid backup code used for user {}", userId);
            recordFailedAttempt(userId);
            return new MfaVerificationResult(false, "Invalid backup code.");
        }
    }

    // --- Internal Helpers ---

    private boolean dispatchOtp(String channel, String destination, String otp) {
        try {
            if ("EMAIL".equalsIgnoreCase(channel)) {
                String message = String.format("Your Merryblue verification code is: %s. It expires in %d minutes.", otp, OTP_VALIDITY_MINUTES);
                emailService.sendSimpleMessage(destination, "Your Authentication Code", message);
                return true;
            } else if ("SMS".equalsIgnoreCase(channel)) {
                String message = String.format("Merryblue code: %s", otp);
                smsService.sendSms(destination, message);
                return true;
            } else if ("AUTHENTICATOR_APP".equalsIgnoreCase(channel)) {
                // No dispatch needed, user reads from their Google Auth app.
                // The OTP generated here would need to be a TOTP generation based on user's shared secret.
                // For this mock, we assume the providedOtp verification logic handles TOTP validation.
                return true; 
            }
        } catch (Exception e) {
            log.error("Failed to dispatch OTP via {}", channel, e);
        }
        return false;
    }

    private String generateNumericOtp(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    private boolean isUserLockedOut(UUID userId) {
        LockoutState state = userLockouts.get(userId);
        if (state == null) return false;
        
        if (state.failedAttempts >= MAX_ATTEMPTS) {
            if (OffsetDateTime.now().isAfter(state.lockedUntil)) {
                // Lockout period expired
                userLockouts.remove(userId);
                return false;
            }
            return true;
        }
        return false;
    }

    private void recordFailedAttempt(UUID userId) {
        userLockouts.compute(userId, (k, v) -> {
            if (v == null) {
                return new LockoutState(1, null);
            }
            v.failedAttempts++;
            if (v.failedAttempts >= MAX_ATTEMPTS) {
                v.lockedUntil = OffsetDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES);
                log.warn("User {} is now locked out of MFA until {}", userId, v.lockedUntil);
            }
            return v;
        });
    }

    private void resetLockout(UUID userId) {
        userLockouts.remove(userId);
    }

    // --- Data Classes ---

    private static class MfaChallenge {
        UUID userId;
        String otp;
        OffsetDateTime expiresAt;
        String channel;

        MfaChallenge(UUID userId, String otp, OffsetDateTime expiresAt, String channel) {
            this.userId = userId;
            this.otp = otp;
            this.expiresAt = expiresAt;
            this.channel = channel;
        }
    }

    private static class LockoutState {
        int failedAttempts;
        OffsetDateTime lockedUntil;

        LockoutState(int failedAttempts, OffsetDateTime lockedUntil) {
            this.failedAttempts = failedAttempts;
            this.lockedUntil = lockedUntil;
        }
    }

    public record MfaInitiationResult(boolean success, UUID challengeId, String message) {}
    public record MfaVerificationResult(boolean success, String message) {}
}
