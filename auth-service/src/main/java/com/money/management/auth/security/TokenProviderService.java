package com.money.management.auth.security;

import org.springframework.security.core.Authentication;

public interface TokenProviderService {

    String createToken(Authentication authentication);

    String getUserNameFromToken(String token);

    boolean validateToken(String authToken);

}
