package com.money.management.auth.util;

import com.money.management.auth.domain.AuthProvider;
import com.money.management.auth.domain.User;

public class UserUtil {

    public static User getUser() {
        User user = new User();
        user.setUsername("test@test.com");
        user.setPassword("password");
        user.setEnabled(false);
        user.setProvider(AuthProvider.LOCAL);

        return user;
    }

}
