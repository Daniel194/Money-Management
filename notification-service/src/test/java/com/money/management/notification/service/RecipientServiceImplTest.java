package com.money.management.notification.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.money.management.notification.NotificationApplication;
import com.money.management.notification.domain.NotificationType;
import com.money.management.notification.domain.Recipient;
import com.money.management.notification.repository.RecipientRepository;
import com.money.management.notification.service.impl.RecipientServiceImpl;
import com.money.management.notification.util.NotificationUtil;
import com.money.management.notification.util.RecipientUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = NotificationApplication.class)
@WebAppConfiguration
public class RecipientServiceImplTest {

    @InjectMocks
    private RecipientServiceImpl recipientService;

    @Mock
    private RecipientRepository repository;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldFindByAccountName() {
        Recipient recipient = RecipientUtil.getRecipient(null);

        when(repository.findByAccountName(recipient.getAccountName())).thenReturn(recipient);
        Recipient found = recipientService.findByAccountName(recipient.getAccountName());

        assertEquals(recipient, found);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToFindRecipientWhenAccountNameIsEmpty() {
        recipientService.findByAccountName("");
    }

    @Test
    public void shouldSaveRecipient() {
        Recipient recipient = RecipientUtil.getRecipient(ImmutableMap.of(
                NotificationType.BACKUP, NotificationUtil.getBackup(),
                NotificationType.REMIND, NotificationUtil.getRemind(null)
        ));
        recipient.setAccountName(null);

        Recipient saved = recipientService.save("test", recipient);

        verify(repository).save(recipient);
        assertNotNull(saved.getScheduledNotifications().get(NotificationType.REMIND).getLastNotified());
        assertEquals("test", saved.getAccountName());
    }

    @Test
    public void shouldFindReadyToNotifyWhenNotificationTypeIsBackup() {
        List<Recipient> recipients = ImmutableList.of(new Recipient());
        when(repository.findReadyForBackup()).thenReturn(recipients);

        List<Recipient> found = recipientService.findReadyToNotify(NotificationType.BACKUP);
        assertEquals(recipients, found);
    }

    @Test
    public void shouldFindReadyToNotifyWhenNotificationTypeIsRemind() {
        List<Recipient> recipients = ImmutableList.of(new Recipient());
        when(repository.findReadyForRemind()).thenReturn(recipients);

        List<Recipient> found = recipientService.findReadyToNotify(NotificationType.REMIND);
        assertEquals(recipients, found);
    }

    @Test
    public void shouldMarkAsNotified() {
        Recipient recipient = RecipientUtil.getRecipient(ImmutableMap.of(
                NotificationType.REMIND, NotificationUtil.getRemind(null)
        ));

        recipientService.markNotified(NotificationType.REMIND, recipient);
        assertNotNull(recipient.getScheduledNotifications().get(NotificationType.REMIND).getLastNotified());
        verify(repository).save(recipient);
    }
}