package com.money.management.auth.service;

import org.springframework.security.core.Authentication;

public interface TokenProvider {

    String createToken(Authentication authentication);

    String getUserNameFromToken(String token);

    boolean validateToken(String authToken);

}
