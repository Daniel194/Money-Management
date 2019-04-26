package com.money.management.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
@EnableAuthorizationServer
class OAuth2AuthorizationConfig extends AuthorizationServerConfigurerAdapter {
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String CLIENT_CREDENTIAL = "client_credentials";
    private static final String SERVER = "server";

    private final TokenStore tokenStore = new InMemoryTokenStore();

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    @Qualifier(BeanIds.AUTHENTICATION_MANAGER)
    private AuthenticationManager authenticationManager;

    @Autowired
    private Environment env;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("browser")
                .authorizedGrantTypes(REFRESH_TOKEN, "password")
                .secret("{noop}")
                .scopes("ui")
                .and()
                .withClient("account-service")
                .secret("{noop}" + env.getProperty("ACCOUNT_SERVICE_PASSWORD"))
                .authorizedGrantTypes(CLIENT_CREDENTIAL, REFRESH_TOKEN)
                .scopes(SERVER)
                .and()
                .withClient("statistics-service")
                .secret("{noop}" + env.getProperty("STATISTICS_SERVICE_PASSWORD"))
                .authorizedGrantTypes(CLIENT_CREDENTIAL, REFRESH_TOKEN)
                .scopes(SERVER)
                .and()
                .withClient("notification-service")
                .secret("{noop}" + env.getProperty("NOTIFICATION_SERVICE_PASSWORD"))
                .authorizedGrantTypes(CLIENT_CREDENTIAL, REFRESH_TOKEN)
                .scopes(SERVER);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                .tokenStore(tokenStore)
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }
}
