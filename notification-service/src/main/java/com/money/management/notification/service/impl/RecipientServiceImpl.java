package com.money.management.notification.service.impl;

import com.money.management.notification.domain.NotificationSettings;
import com.money.management.notification.domain.NotificationType;
import com.money.management.notification.repository.RecipientRepository;
import com.money.management.notification.domain.Recipient;
import com.money.management.notification.service.RecipientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

@Service
public class RecipientServiceImpl implements RecipientService {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private RecipientRepository repository;

    @Autowired
    public RecipientServiceImpl(RecipientRepository repository) {
        this.repository = repository;
    }

    @Override
    public Recipient findByAccountName(String accountName) {
        Assert.hasLength(accountName, "Account Name is required");
        return repository.findByAccountName(accountName);
    }

    @Override
    public Recipient save(String accountName, Recipient recipient) {
        recipient.setAccountName(accountName);
        recipient.getScheduledNotifications().values().forEach(this::setSettings);

        repository.save(recipient);

        log.info("Recipient {} settings has been updated", recipient);

        return recipient;
    }

    @Override
    public List<Recipient> findReadyToNotify(NotificationType type) {
        switch (type) {
            case BACKUP:
                return repository.findReadyForBackup();
            case REMIND:
                return repository.findReadyForRemind();
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void markNotified(NotificationType type, Recipient recipient) {
        recipient.getScheduledNotifications().get(type).setLastNotified(new Date());
        repository.save(recipient);
    }

    private void setSettings(NotificationSettings settings) {
        if (settings.getLastNotified() == null) {
            settings.setLastNotified(new Date());
        }
    }

}
