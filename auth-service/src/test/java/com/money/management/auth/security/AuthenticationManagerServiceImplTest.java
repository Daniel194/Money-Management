package com.money.management.auth.security;

import com.money.management.auth.AuthApplication;
import com.money.management.auth.payload.LoginRequest;
import com.money.management.auth.security.impl.AuthenticationManagerServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AuthApplication.class)
public class AuthenticationManagerServiceImplTest {

    @InjectMocks
    private AuthenticationManagerServiceImpl authenticationManagerService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldAuthenticateUser() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword("12345");
        loginRequest.setEmail("test@test.com");

        when(authenticationManager.authenticate(any())).thenReturn(new TestingAuthenticationToken(null, null));

        assertNotNull(authenticationManagerService.authenticate(loginRequest));
    }

}
