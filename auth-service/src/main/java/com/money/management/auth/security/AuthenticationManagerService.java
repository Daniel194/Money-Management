package com.money.management.auth.security;

import com.money.management.auth.payload.LoginRequest;
import org.springframework.security.core.Authentication;

public interface AuthenticationManagerService {

    Authentication authenticate(LoginRequest loginRequest);

}
