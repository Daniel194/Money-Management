package com.money.management.notification.controller;

import com.money.management.notification.domain.Recipient;
import com.money.management.notification.service.RecipientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/recipients")
public class RecipientController {

    private RecipientService recipientService;

    @Autowired
    public RecipientController(RecipientService recipientService) {
        this.recipientService = recipientService;
    }

    @RequestMapping(path = "/current", method = RequestMethod.GET)
    public Recipient getCurrentNotificationsSettings(Principal principal) {
        return recipientService.findByAccountName(principal.getName());
    }

    @RequestMapping(path = "/current", method = RequestMethod.PUT)
    public Recipient saveCurrentNotificationsSettings(Principal principal, @Valid @RequestBody Recipient recipient) {
        return recipientService.save(principal.getName(), recipient);
    }
}
