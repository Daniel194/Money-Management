package com.money.management.auth.service;

import com.money.management.auth.domain.User;
import com.money.management.auth.payload.SignUpRequest;

public interface UserService {

    void create(User user);

    void create(SignUpRequest signUpRequest);

    void changePassword(String name, String password);
}
