package com.money.management.auth.security;

import com.money.management.auth.AuthApplication;
import com.money.management.auth.config.AppProperties;
import com.money.management.auth.domain.User;
import com.money.management.auth.security.impl.TokenProviderServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AuthApplication.class)
public class TokenProviderServiceImplTest {

    @InjectMocks
    private TokenProviderServiceImpl service;

    @Mock
    private AppProperties appProperties;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldReturnNewToken() {
        User user = new User();
        user.setUsername("test");

        TestingAuthenticationToken authenticationToken = new TestingAuthenticationToken(user, null);

        when(appProperties.getAuth()).thenReturn(getAuth());

        assertNotNull(service.createToken(authenticationToken));
    }

    @Test
    public void shouldNotValidateToken() {
        when(appProperties.getAuth()).thenReturn(getAuth());

        assertFalse(service.validateToken(""));
    }

    private AppProperties.Auth getAuth() {
        AppProperties.Auth auth = new AppProperties.Auth();
        auth.setTokenExpirationMsec(12345L);
        auth.setTokenSecret("12345");

        return auth;
    }
}
