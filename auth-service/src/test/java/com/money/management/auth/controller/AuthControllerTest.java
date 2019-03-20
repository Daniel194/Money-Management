package com.money.management.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.money.management.auth.AuthApplication;
import com.money.management.auth.payload.LoginRequest;
import com.money.management.auth.payload.ResetPasswordRequest;
import com.money.management.auth.payload.SignUpRequest;
import com.money.management.auth.security.AuthenticationManagerService;
import com.money.management.auth.security.TokenProviderService;
import com.money.management.auth.service.ForgotPasswordService;
import com.money.management.auth.service.UserService;
import com.money.management.auth.service.VerificationTokenService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AuthApplication.class)
@WebAppConfiguration
public class AuthControllerTest {

    private static final String MESSAGE = "TEST";
    private static final String EMAIL = "test@test.com";

    private static final ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private AuthController authController;

    @Mock
    private VerificationTokenService verificationTokenService;

    @Mock
    private AuthenticationManagerService authenticationManagerService;

    @Mock
    private TokenProviderService tokenProviderService;

    @Mock
    private UserService userService;

    @Mock
    private ForgotPasswordService forgotPasswordService;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    public void shouldLoginUser() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(EMAIL);
        loginRequest.setPassword("12345");

        when(authenticationManagerService.authenticate(loginRequest)).thenReturn(null);
        when(tokenProviderService.createToken(null)).thenReturn("1234567");

        String json = mapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.accessToken").value("1234567"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldCreateNewUser() throws Exception {
        SignUpRequest request = new SignUpRequest();
        request.setEmail(EMAIL);
        request.setPassword("12345");

        String json = mapper.writeValueAsString(request);

        mockMvc.perform(post("/auth/sign-up").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.message").value("User registered successfully !"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnConfirmationMessage() throws Exception {
        when(verificationTokenService.enableUser("12345")).thenReturn(MESSAGE);

        mockMvc.perform(get("/auth/verification?token=12345"))
                .andExpect(jsonPath("$.message").value(MESSAGE))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldResendVerificationEmail() throws Exception {
        mockMvc.perform(get("/auth/verification/resend?email=" + EMAIL))
                .andExpect(jsonPath("$.message").value("The verification email has been resent !"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldSendForgotPasswordUrl() throws Exception {
        mockMvc.perform(get("/auth/password/forgot?email=" + EMAIL))
                .andExpect(jsonPath("$.message").value("An email has been sent !"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldResetPassword() throws Exception {
        ResetPasswordRequest resetPassword = new ResetPasswordRequest();
        resetPassword.setPassword("password01");
        resetPassword.setToken("12345");

        String json = mapper.writeValueAsString(resetPassword);

        mockMvc.perform(put("/auth/password/forgot").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(jsonPath("$.message").value("The password was changed successfully !"))
                .andExpect(status().isOk());
    }

}
