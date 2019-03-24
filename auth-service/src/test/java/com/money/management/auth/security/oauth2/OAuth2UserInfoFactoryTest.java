package com.money.management.auth.security.oauth2;

import com.money.management.auth.security.oauth2.user.FacebookOAuth2UserInfo;
import com.money.management.auth.security.oauth2.user.GoogleOAuth2UserInfo;
import com.money.management.auth.security.oauth2.user.OAuth2UserInfo;
import com.money.management.auth.security.oauth2.user.OAuth2UserInfoFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class OAuth2UserInfoFactoryTest {

    @Test
    public void shouldReturnGoogle() {
        OAuth2UserInfo google = OAuth2UserInfoFactory.getOAuth2UserInfo("GOOGLE", new HashMap<>());
        OAuth2UserInfo facebook = OAuth2UserInfoFactory.getOAuth2UserInfo("FACEBOOK", new HashMap<>());

        Assert.assertTrue(google instanceof GoogleOAuth2UserInfo);
        Assert.assertTrue(facebook instanceof FacebookOAuth2UserInfo);
    }

}
