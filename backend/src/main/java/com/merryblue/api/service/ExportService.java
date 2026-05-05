package com.merryblue.api.service;

import com.merryblue.api.model.Contact;
import com.merryblue.api.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final ContactRepository contactRepository;

    public String exportContactsToCsv() {
        List<Contact> contacts = contactRepository.findAll();
        StringBuilder csv = new StringBuilder("Name,Email,Subject,CreatedAt\n");
        for (Contact contact : contacts) {
            csv.append(contact.getName()).append(",")
               .append(contact.getEmail()).append(",")
               .append(contact.getSubject()).append(",")
               .append(contact.getCreatedAt()).append("\n");
        }
        return csv.toString();
    }
}
