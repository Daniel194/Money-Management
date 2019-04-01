package com.money.management.auth.service;

import com.money.management.auth.AuthApplication;
import com.money.management.auth.repository.UserRepository;
import com.money.management.auth.domain.User;
import com.money.management.auth.service.impl.UserServiceImpl;
import com.money.management.auth.util.UserUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AuthApplication.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private UserRepository repository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldUpdateUserPassword() {
        User user = UserUtil.getUser();

        when(repository.findUsersByUsername(user.getUsername())).thenReturn(Optional.of(user));

        userService.changePassword(user.getUsername(), "12345");

        verify(repository, times(1)).save(user);
    }

}
