package com.money.management.auth.service;

import com.money.management.auth.domain.User;
import com.money.management.auth.domain.VerificationToken;

public interface VerificationTokenService {

    VerificationToken create(User user);

    String enableUser(String token);

    void resendMailVerification(String email);
}
