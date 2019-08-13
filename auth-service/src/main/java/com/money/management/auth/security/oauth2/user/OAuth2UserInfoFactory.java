package com.money.management.auth.security.oauth2.user;

import com.money.management.auth.domain.AuthProvider;
import com.money.management.auth.exception.OAuth2AuthenticationProcessingException;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {

        switch (AuthProvider.getProvider(registrationId)) {
            case GOOGLE:
                return new GoogleOAuth2UserInfo(attributes);
            case FACEBOOK:
                return new FacebookOAuth2UserInfo(attributes);
            case TWITTER:
                //TODO
                return null;
            default:
                throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }

}
