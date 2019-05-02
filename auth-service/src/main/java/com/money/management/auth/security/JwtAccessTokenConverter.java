package com.money.management.auth.security;

import com.money.management.auth.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAccessTokenConverter extends org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter {

    private AppProperties appProperties;
    private UserDetailsService userDetailsService;

    @Autowired
    public JwtAccessTokenConverter(AppProperties appProperties, UserDetailsService userDetailsService) {
        this.appProperties = appProperties;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Map<String, Object> decode(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(appProperties.getAuth().getTokenSecret())
                .parseClaimsJws(token)
                .getBody();

        Map<String, Object> map = new HashMap<>();
        map.put(Claims.ID, claims.getId());
        map.put(Claims.SUBJECT, claims.getSubject());
        map.put(Claims.AUDIENCE, claims.getAudience());
        map.put(Claims.EXPIRATION, claims.getExpiration());
        map.put(Claims.ISSUED_AT, claims.getIssuedAt());
        map.put(Claims.ISSUER, claims.getIssuer());
        map.put(Claims.NOT_BEFORE, claims.getNotBefore());

        return map;
    }

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        String userName = (String) map.get(Claims.SUBJECT);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

        UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        OAuth2Request request = new OAuth2Request(null, null, null, true, Collections.emptySet(), null, null, null,
                null);

        return new OAuth2Authentication(request, user);
    }

    @Override
    public boolean isRefreshToken(OAuth2AccessToken token) {
        return false;
    }

    @Override
    public OAuth2AccessToken extractAccessToken(String value, Map<String, ?> map) {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(value);
        Map<String, Object> info = new HashMap<>(map);
        info.remove(EXP);
        info.remove(AUD);

        if (map.containsKey(EXP)) {
            token.setExpiration((Date) map.get(EXP));
        }

        token.setScope(Collections.emptySet());
        token.setAdditionalInformation(info);
        return token;
    }

}
