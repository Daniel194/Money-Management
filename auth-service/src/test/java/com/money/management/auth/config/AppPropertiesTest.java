package com.money.management.auth.config;

import com.money.management.auth.AuthApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AuthApplication.class)
public class AppPropertiesTest {

    @Autowired
    private AppProperties properties;

    @Test
    public void shouldLoadProperties() {
        assertNotNull(properties.getAuth());
        assertNotEquals(0, properties.getAuth().getTokenExpirationMsec());
        assertNotNull(properties.getAuth().getTokenSecret());

        assertNotNull(properties.getOauth2());
        assertNotNull(properties.getOauth2().getAuthorizedRedirectUris());
        assertEquals(1, properties.getOauth2().getAuthorizedRedirectUris().size());
    }


}
