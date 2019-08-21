package com.money.management.account.service.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

public class SecurityHolderOAuth2AccessToken extends DefaultOAuth2AccessToken {
    private String value;

    public SecurityHolderOAuth2AccessToken() {
        super("");
    }

    @Override
    public String getValue() {
        if (value == null || value.isEmpty()) {
            value = getAccessToken();
        }

        return value;
    }

    private String getAccessToken() {
        OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
        Object details = authentication.getDetails();

        if (details instanceof OAuth2AuthenticationDetails) {
            OAuth2AuthenticationDetails oauthDetails = (OAuth2AuthenticationDetails) details;
            return oauthDetails.getTokenValue();
        }

        return null;
    }

}
