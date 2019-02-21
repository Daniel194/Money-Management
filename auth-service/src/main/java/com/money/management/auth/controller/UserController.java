package com.money.management.auth.controller;

import com.money.management.auth.payload.ResetPasswordRequest;
import com.money.management.auth.domain.User;
import com.money.management.auth.service.ForgotPasswordService;
import com.money.management.auth.service.UserService;
import com.money.management.auth.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;
    private VerificationTokenService verificationTokenService;
    private ForgotPasswordService forgotPasswordService;

    @Autowired
    public UserController(UserService userService,
                          VerificationTokenService verificationTokenService,
                          ForgotPasswordService forgotPasswordService) {
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
        this.forgotPasswordService = forgotPasswordService;
    }

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public Principal getUser(Principal principal) {
        return principal;
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

    @RequestMapping(value = "/change/password", method = RequestMethod.POST)
    public void changePassword(Principal principal, @Valid @RequestBody String password) {
        userService.changePassword(principal.getName(), password);
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void createUser(@Valid @RequestBody User user) {
        userService.create(user);
    }
}
