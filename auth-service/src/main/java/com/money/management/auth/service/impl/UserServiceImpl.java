package com.money.management.auth.service.impl;

import com.money.management.auth.domain.AuthProvider;
import com.money.management.auth.exception.BadRequestException;
import com.money.management.auth.payload.SignUpRequest;
import com.money.management.auth.repository.UserRepository;
import com.money.management.auth.domain.User;
import com.money.management.auth.listener.event.OnRegistrationCompleteEvent;
import com.money.management.auth.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserServiceImpl implements UserService {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private PasswordEncoder encoder;
    private UserRepository repository;
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    public UserServiceImpl(UserRepository repository, PasswordEncoder encoder, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.encoder = encoder;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void create(SignUpRequest signUpRequest) {
        User existing = repository.findUsersByUsername(signUpRequest.getEmail()).orElse(null);

        if (existing != null) {
            throw new BadRequestException("User already exists: " + existing.getUsername());
        }

        User user = createUser(signUpRequest);

        repository.save(user);
        log.info("New user has been created: {}", user.getUsername());

        sendVerificationEmail(user);
    }

    @Override
    public void changePassword(String name, String password) {
        User user = repository.findUsersByUsername(name).orElseThrow(() -> new BadRequestException("User doesn't exist !"));
        user.setPassword(encoder.encode(password));

        repository.save(user);
    }

    private void sendVerificationEmail(User user) {
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));
    }

    private User createUser(SignUpRequest signUpRequest) {
        User user = new User();
        user.setPassword(signUpRequest.getPassword());
        user.setUsername(signUpRequest.getEmail());
        user.setAuthorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        setUserValues(user);

        return user;
    }

    private void setUserValues(User user) {
        String hash = encoder.encode(user.getPassword());
        user.setPassword(hash);
        user.setEnabled(false);
        user.setProvider(AuthProvider.LOCAL);
    }

}
