package com.money.management.auth.controller;


import com.money.management.auth.payload.ApiResponse;
import com.money.management.auth.payload.AuthResponse;
import com.money.management.auth.payload.LoginRequest;
import com.money.management.auth.payload.SignUpRequest;
import com.money.management.auth.security.TokenProviderService;
import com.money.management.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;
    private TokenProviderService tokenProviderService;
    private UserService userService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          TokenProviderService tokenProviderService,
                          UserService userService) {

        this.authenticationManager = authenticationManager;
        this.tokenProviderService = tokenProviderService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProviderService.createToken(authentication);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

        userService.create(signUpRequest);

        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully !"));
    }

}