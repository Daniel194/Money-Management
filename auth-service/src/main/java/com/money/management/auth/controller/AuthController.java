package com.money.management.auth.controller;


import com.money.management.auth.payload.*;
import com.money.management.auth.security.AuthenticationManagerService;
import com.money.management.auth.security.TokenProviderService;
import com.money.management.auth.service.ForgotPasswordService;
import com.money.management.auth.service.UserService;
import com.money.management.auth.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public AuthResponse authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManagerService.authenticate(loginRequest);
        String token = tokenProviderService.createToken(authentication);

        return new AuthResponse(token);
    }

    @PostMapping("/sign-up")
    public ApiResponse registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        userService.create(signUpRequest);

        return new ApiResponse(true, "User registered successfully !");
    }

    @RequestMapping(value = "/verification", method = RequestMethod.GET)
    public ApiResponse mailVerification(@RequestParam("token") String token) {
        String message = verificationTokenService.enableUser(token);

        return new ApiResponse(true, message);
    }

    @RequestMapping(value = "/verification/resend", method = RequestMethod.GET)
    public ApiResponse resendMailVerification(@RequestParam("email") String email) {
        verificationTokenService.resendMailVerification(email);

        return new ApiResponse(true, "The verification email has been resent !");
    }

    @RequestMapping(value = "/password/forgot", method = RequestMethod.GET)
    public ApiResponse sendForgotPasswordMail(@RequestParam("email") String email) {
        forgotPasswordService.sendEmail(email);

        return new ApiResponse(true, "An email has been sent !");
    }

    @RequestMapping(value = "/password/forgot", method = RequestMethod.PUT)
    public ApiResponse resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        forgotPasswordService.resetPassword(resetPasswordRequest);

        return new ApiResponse(true, "The password was changed successfully !");
    }

}