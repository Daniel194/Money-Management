package com.money.management.notification.service.impl;

import com.money.management.notification.client.AccountServiceClient;
import com.money.management.notification.domain.NotificationType;
import com.money.management.notification.domain.Recipient;
import com.money.management.notification.service.EmailService;
import com.money.management.notification.service.NotificationService;
import com.money.management.notification.service.RecipientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private AccountServiceClient client;
    private RecipientService recipientService;
    private EmailService emailService;

    @Autowired
    private NotificationServiceImpl(AccountServiceClient client, RecipientService recipientService, EmailService emailService) {
        this.client = client;
        this.recipientService = recipientService;
        this.emailService = emailService;
    }

    @Override
    @Scheduled(cron = "${backup.cron}")
    public void sendBackupNotifications() {
        NotificationType type = NotificationType.BACKUP;

        List<Recipient> recipients = recipientService.findReadyToNotify(type);
        log.info("Found {} recipients for backup notification", recipients.size());

        recipients.forEach(recipient -> CompletableFuture.runAsync(() -> sendEmailWithAttachment(type, recipient)));
    }

    @Override
    @Scheduled(cron = "${remind.cron}")
    public void sendRemindNotifications() {
        NotificationType type = NotificationType.REMIND;

        List<Recipient> recipients = recipientService.findReadyToNotify(type);
        log.info("Found {} recipients for remind notification", recipients.size());

        recipients.forEach(recipient -> CompletableFuture.runAsync(() -> sendEmail(type, recipient)));
    }

    private void sendEmail(NotificationType type, Recipient recipient) {
        try {
            emailService.send(type, recipient, null);
            recipientService.markNotified(type, recipient);
        } catch (Exception e) {
            log.error("An error during remind notification for {}", recipient, e);
        }
    }

    private void sendEmailWithAttachment(NotificationType type, Recipient recipient) {
        try {
            String attachment = client.getAccount(recipient.getAccountName());
            emailService.send(type, recipient, attachment);
            recipientService.markNotified(type, recipient);
        } catch (Exception e) {
            log.error("An error during backup notification for {}", recipient, e);
        }
    }

}
