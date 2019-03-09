package com.money.management.auth.controller;

import com.money.management.auth.service.UserService;
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
    @PreAuthorize("hasRole('USER')")
    public Principal getUser(Principal principal) {
        return principal;
    }

    @RequestMapping(value = "/change/password", method = RequestMethod.POST)
    @PreAuthorize("hasRole('USER')")
    public void changePassword(Principal principal, @Valid @RequestBody String password) {
        userService.changePassword(principal.getName(), password);
    }
}
