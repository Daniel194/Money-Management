package com.money.management.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.money.management.account.AccountApplication;
import com.money.management.account.domain.*;
import com.money.management.account.service.AccountService;
import com.money.management.account.util.AccountUtil;
import com.money.management.account.util.ItemUtil;
import com.sun.security.auth.UserPrincipal;
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
@SpringBootTest(classes = AccountApplication.class)
@WebAppConfiguration
public class AccountControllerTest {

    private static final ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountService accountService;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    @Test
    public void shouldGetAccountByName() throws Exception {
        Account account = new Account();
        account.setName("test@test.com");

        when(accountService.findByName(account.getName())).thenReturn(account);

        mockMvc.perform(get("/find?name=" + account.getName()))
                .andExpect(jsonPath("$.name").value(account.getName()))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetCurrentAccount() throws Exception {
        Account account = new Account();
        account.setName("test@test.com");

        UserPrincipal userPrincipal = new UserPrincipal(account.getName());

        when(accountService.findByName(userPrincipal)).thenReturn(account);

        mockMvc.perform(get("/current").principal(userPrincipal))
                .andExpect(jsonPath("$.name").value(account.getName()))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldSaveCurrentAccount() throws Exception {
        Account account = AccountUtil.getAccount(ItemUtil.getGrocery());

        String json = mapper.writeValueAsString(account);

        mockMvc.perform(put("/current").principal(new UserPrincipal(account.getName()))
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldFailOnValidationTryingToSaveCurrentAccount() throws Exception {
        Account account = new Account();
        account.setName("test@test.com");

        String json = mapper.writeValueAsString(account);

        mockMvc.perform(put("/current").principal(new UserPrincipal(account.getName()))
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());
    }
}
