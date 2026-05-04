package com.merryblue.api.controller;

import com.merryblue.api.model.Contact;
import com.merryblue.api.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactRepository contactRepository;

    @PostMapping
    public Contact submitContact(@RequestBody Contact contact) {
        contact.setIsRead(false);
        return contactRepository.save(contact);
    }
}
