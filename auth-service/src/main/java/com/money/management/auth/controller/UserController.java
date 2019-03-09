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
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public Principal getUser(Principal principal) {
        return principal;
    }

    @RequestMapping(value = "/change/password", method = RequestMethod.POST)
    public void changePassword(Principal principal, @Valid @RequestBody String password) {
        userService.changePassword(principal.getName(), password);
    }
}
