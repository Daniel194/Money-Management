package com.money.management.auth.security;

import com.money.management.auth.AuthApplication;
import com.money.management.auth.domain.AuthProvider;
import com.money.management.auth.repository.UserRepository;
import com.money.management.auth.domain.User;
import com.money.management.auth.security.impl.UserDetailsServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AuthApplication.class)
public class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl service;

    @Mock
    private UserRepository repository;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldLoadByUsernameWhenUserExists() {
        User user = new User();
        user.setProvider(AuthProvider.LOCAL);

        when(repository.findUsersByUsername(any())).thenReturn(Optional.of(user));
        UserDetails loaded = service.loadUserByUsername("name");

        assertEquals(user, loaded);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void shouldFailToLoadUserFromOtherProvider() {
        User user = new User();
        user.setProvider(AuthProvider.GOOGLE);

        when(repository.findUsersByUsername(any())).thenReturn(Optional.of(user));
        UserDetails loaded = service.loadUserByUsername("name");
    }

    @Test(expected = UsernameNotFoundException.class)
    public void shouldFailToLoadByUsernameWhenUserNotExists() {
        service.loadUserByUsername("name");
    }
}