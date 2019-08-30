package com.money.management.auth.service.impl;

import com.money.management.auth.domain.AuthProvider;
import com.money.management.auth.domain.ForgotPasswordToken;
import com.money.management.auth.exception.BadRequestException;
import com.money.management.auth.payload.ResetPasswordRequest;
import com.money.management.auth.domain.User;
import com.money.management.auth.listener.event.OnForgotPasswordCompleteEvent;
import com.money.management.auth.repository.ForgotPasswordTokenRepository;
import com.money.management.auth.repository.UserRepository;
import com.money.management.auth.service.ForgotPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    private UserRepository userRepository;
    private ApplicationEventPublisher eventPublisher;
    private ForgotPasswordTokenRepository repository;
    private PasswordEncoder encoder;

    @Autowired
    public ForgotPasswordServiceImpl(UserRepository userRepository,
                                     ApplicationEventPublisher eventPublisher,
                                     PasswordEncoder encoder,
                                     ForgotPasswordTokenRepository repository) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
    public void sendEmail(String email) {
        User user = userRepository.findUsersByUsername(email)
                .filter(u -> AuthProvider.LOCAL.equals(u.getProvider()))
                .orElseThrow(() -> new BadRequestException("User doesn't exist, please register !"));

        if (!user.isEnabled()) {
            throw new BadRequestException("The user isn't enabled !");
        }

        eventPublisher.publishEvent(new OnForgotPasswordCompleteEvent(user));
    }

    @Override
    public ForgotPasswordToken createToken(User user) {
        ForgotPasswordToken token = new ForgotPasswordToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpireDate(LocalDateTime.now().plusDays(1));

        repository.save(token);

        return token;
    }

    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        Optional<ForgotPasswordToken> optional = repository.findById(resetPasswordRequest.getToken());

        if (optional.isEmpty()) {
            throw new BadRequestException("The token is invalid !");
        }

        ForgotPasswordToken token = optional.get();

        if (token.getExpireDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Forgot password toke has expired !");
        }

        updateUserPassword(token.getUser(), resetPasswordRequest.getPassword());
    }

    private void updateUserPassword(User user, String password) {
        user.setPassword(encoder.encode(password));
        userRepository.save(user);
    }

}
