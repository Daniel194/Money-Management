package com.money.management.auth.security.oauth2;

import com.money.management.auth.domain.AuthProvider;
import com.money.management.auth.domain.User;
import com.money.management.auth.exception.OAuth2AuthenticationProcessingException;
import com.money.management.auth.repository.UserRepository;
import com.money.management.auth.security.oauth2.user.OAuth2UserInfo;
import com.money.management.auth.security.oauth2.user.OAuth2UserInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Optional;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    private UserRepository userRepository;

    @Autowired
    public OAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        String email = getEmail(oAuth2UserInfo);
        Optional<User> userOptional = userRepository.findUsersByUsername(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            verifyUserProvider(user, oAuth2UserRequest);
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        user.setAttributes(oAuth2User.getAttributes());

        return user;
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = new User();

        user.setProvider(AuthProvider.getProvider(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
        user.setProviderId(oAuth2UserInfo.getId());
        user.setUsername(oAuth2UserInfo.getEmail());

        user.setEnabled(true);
        user.setAuthorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        return userRepository.save(user);
    }

    private String getEmail(OAuth2UserInfo oAuth2UserInfo) {
        if (oAuth2UserInfo == null || StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        return oAuth2UserInfo.getEmail();
    }

    private void verifyUserProvider(User user, OAuth2UserRequest oAuth2UserRequest) {
        if (!user.getProvider().equals(AuthProvider.getProvider(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
            throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                    user.getProvider() + " account. Please use your " + user.getProvider() +
                    " account to login.");
        }
    }

}
