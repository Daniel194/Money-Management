package com.money.management.auth.service;

import com.money.management.auth.AuthApplication;
import com.money.management.auth.domain.ForgotPasswordToken;
import com.money.management.auth.domain.User;
import com.money.management.auth.exception.BadRequestException;
import com.money.management.auth.payload.ResetPasswordRequest;
import com.money.management.auth.repository.ForgotPasswordTokenRepository;
import com.money.management.auth.repository.UserRepository;
import com.money.management.auth.service.impl.ForgotPasswordServiceImpl;
import com.money.management.auth.util.UserUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AuthApplication.class)
public class ForgotPasswordServiceTest {

    @InjectMocks
    private ForgotPasswordServiceImpl service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ForgotPasswordTokenRepository repository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private PasswordEncoder encoder;

    @Test(expected = BadRequestException.class)
    public void shouldNotSendEmailForUnregisteredUser() {
        String email = "test@test.com";

        when(userRepository.findUsersByUsername(email)).thenReturn(null);

        service.sendEmail(email);
    }

    @Test(expected = BadRequestException.class)
    public void shouldNotSendEmailToNotEnabledUser() {
        User user = UserUtil.getUser();

        when(userRepository.findUsersByUsername(user.getUsername())).thenReturn(user);

        service.sendEmail(user.getUsername());
    }

    @Test
    public void shouldSendEmailForResetPassword() {
        User user = UserUtil.getUser();
        user.setEnabled(true);

        when(userRepository.findUsersByUsername(user.getUsername())).thenReturn(user);

        service.sendEmail(user.getUsername());
    }

    @Test
    public void shouldCreateUser() {
        User user = UserUtil.getUser();
        ForgotPasswordToken token = service.createToken(user);

        verify(repository, times(1)).save(token);

        assertThat(token.getUser(), is(user));
        assertThat(token.getExpireDate().getDayOfYear(), is(LocalDateTime.now().plusDays(1).getDayOfYear()));
    }

    @Test(expected = BadRequestException.class)
    public void shouldNotResetPasswordForInvalidToken() {
        ResetPasswordRequest resetPassword = getResetPassword();

        when(repository.findById(resetPassword.getToken())).thenReturn(Optional.empty());

        service.resetPassword(resetPassword);
    }

    @Test(expected = BadRequestException.class)
    public void shouldNotResetPasswordForExpiredToken() {
        ResetPasswordRequest resetPassword = getResetPassword();

        ForgotPasswordToken token = new ForgotPasswordToken();
        token.setExpireDate(LocalDateTime.now().plusDays(-1));

        when(repository.findById(resetPassword.getToken())).thenReturn(Optional.of(token));

        service.resetPassword(resetPassword);
    }

    @Test
    public void shouldResetPassword() {
        ResetPasswordRequest resetPassword = getResetPassword();
        User user = UserUtil.getUser();

        ForgotPasswordToken token = new ForgotPasswordToken();
        token.setExpireDate(LocalDateTime.now().plusDays(1));
        token.setUser(user);
        token.setToken(resetPassword.getToken());

        when(repository.findById(resetPassword.getToken())).thenReturn(Optional.of(token));

        service.resetPassword(resetPassword);
    }

    private ResetPasswordRequest getResetPassword() {
        ResetPasswordRequest resetPassword = new ResetPasswordRequest();
        resetPassword.setToken("12345");
        resetPassword.setPassword("12345");

        return resetPassword;
    }

}
