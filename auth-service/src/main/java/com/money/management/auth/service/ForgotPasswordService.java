package com.money.management.auth.service;

import com.money.management.auth.domain.ForgotPasswordToken;
import com.money.management.auth.payload.ResetPasswordRequest;
import com.money.management.auth.domain.User;

public interface ForgotPasswordService {
    void sendEmail(String email);

    ForgotPasswordToken createToken(User user);

    void resetPassword(ResetPasswordRequest resetPasswordRequest);
}
