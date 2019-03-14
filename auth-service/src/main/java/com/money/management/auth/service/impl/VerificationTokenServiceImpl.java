package com.money.management.auth.service.impl;

import com.money.management.auth.domain.User;
import com.money.management.auth.domain.VerificationToken;
import com.money.management.auth.exception.BadRequestException;
import com.money.management.auth.listener.event.OnResendVerificationEmailCompleteEvent;
import com.money.management.auth.repository.UserRepository;
import com.money.management.auth.repository.VerificationTokenRepository;
import com.money.management.auth.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private VerificationTokenRepository verificationTokenRepository;
    private UserRepository userRepository;
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    public VerificationTokenServiceImpl(VerificationTokenRepository verificationTokenRepository,
                                        UserRepository userRepository,
                                        ApplicationEventPublisher eventPublisher) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public VerificationToken create(User user) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setExpireDate(LocalDateTime.now().plusDays(1));
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);

        return verificationToken;
    }

    @Override
    public String enableUser(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

        verifyToken(verificationToken);

        return enableUser(verificationToken.getUser());
    }

    @Override
    public void resendMailVerification(String email) {
        verifyUser(email);

        VerificationToken verificationToken = updateUserVerificationToken(email);
        eventPublisher.publishEvent(new OnResendVerificationEmailCompleteEvent(verificationToken));
    }

    private void verifyToken(VerificationToken verificationToken) {
        if (verificationToken == null) {
            throw new BadRequestException("Invalid Token !");
        }

        if (verificationToken.getExpireDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Verification toke has expired !");
        }
    }

    private String enableUser(User user) {
        if (user.isEnabled()) {
            return "The user is already enabled !";
        }

        user.setEnabled(true);
        userRepository.save(user);

        return "The user was enabled, you can now login in the application !";
    }

    private void verifyUser(String username) {
        User user = userRepository.findUsersByUsername(username);

        if (user == null) {
            throw new BadRequestException("User doesn't exist, please register !");
        }

        if (user.isEnabled()) {
            throw new BadRequestException("The user was already enabled !");
        }
    }

    private VerificationToken updateUserVerificationToken(String email) {
        VerificationToken verificationToken = verificationTokenRepository.findByUserUsername(email);
        verificationToken.setExpireDate(LocalDateTime.now().plusDays(1));

        verificationTokenRepository.save(verificationToken);

        return verificationToken;
    }

}
