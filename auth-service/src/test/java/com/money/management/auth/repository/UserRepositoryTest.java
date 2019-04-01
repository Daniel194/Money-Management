package com.money.management.auth.repository;

import com.money.management.auth.AuthApplication;
import com.money.management.auth.domain.User;
import com.money.management.auth.util.UserUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AuthApplication.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Test
    public void shouldSaveAndFindUserByName() {
        User user = UserUtil.getUser();

        repository.save(user);

        User found = repository.findUsersByUsername(user.getUsername()).orElse(null);
        assertNotNull("The user doesn't exist !", found);
        assertEquals(user.getUsername(), found.getUsername());
        assertEquals(user.getPassword(), found.getPassword());
    }
}
