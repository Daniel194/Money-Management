package com.money.management.auth.controller;


import com.money.management.auth.payload.*;
import com.money.management.auth.security.AuthenticationManagerService;
import com.money.management.auth.security.TokenProviderService;
import com.money.management.auth.service.ForgotPasswordService;
import com.money.management.auth.service.UserService;
import com.money.management.auth.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthenticationManagerService authenticationManagerService;
    private TokenProviderService tokenProviderService;
    private UserService userService;
    private VerificationTokenService verificationTokenService;
    private ForgotPasswordService forgotPasswordService;

    @Autowired
    public AuthController(AuthenticationManagerService authenticationManagerService,
                          TokenProviderService tokenProviderService,
                          UserService userService,
                          VerificationTokenService verificationTokenService,
                          ForgotPasswordService forgotPasswordService) {

        this.authenticationManagerService = authenticationManagerService;
        this.tokenProviderService = tokenProviderService;
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
        this.forgotPasswordService = forgotPasswordService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManagerService.authenticate(loginRequest);
        String token = tokenProviderService.createToken(authentication);

        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        userService.create(signUpRequest);

        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully !"));
    }

    @RequestMapping(value = "/verification", method = RequestMethod.GET)
    public String mailVerification(@RequestParam("token") String token) {
        return verificationTokenService.enableUser(token);
    }

    @RequestMapping(value = "/verification/resend", method = RequestMethod.GET)
    public String resendMailVerification(@RequestParam("email") String email) {
        return verificationTokenService.resendMailVerification(email);
    }

    @RequestMapping(value = "/password/forgot", method = RequestMethod.GET)
    public String sendForgotPasswordMail(@RequestParam("email") String email) {
        return forgotPasswordService.sendEmail(email);
    }

    @RequestMapping(value = "/password/forgot", method = RequestMethod.PUT)
    public String resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        return forgotPasswordService.resetPassword(resetPasswordRequest);
    }

}