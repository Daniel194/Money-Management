package com.money.management.auth.service;

import com.money.management.auth.payload.SignUpRequest;

public interface UserService {

    void create(SignUpRequest signUpRequest);

    void changePassword(String name, String password);
}
