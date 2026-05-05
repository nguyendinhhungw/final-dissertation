package com.merryblue.api.mapper;

import com.merryblue.api.dto.ContactDTO;
import com.merryblue.api.model.Contact;
import org.springframework.stereotype.Component;

@Component
public class ContactMapper {

    public ContactDTO toDTO(Contact contact) {
        if (contact == null) return null;
        ContactDTO dto = new ContactDTO();
        dto.setId(contact.getId());
        dto.setName(contact.getName());
        dto.setEmail(contact.getEmail());
        dto.setPhone(contact.getPhone());
        dto.setSubject(contact.getSubject());
        dto.setMessage(contact.getMessage());
        dto.setIsRead(contact.getIsRead());
        dto.setCreatedAt(contact.getCreatedAt());
        return dto;
    }

    public Contact toEntity(ContactDTO dto) {
        if (dto == null) return null;
        Contact contact = new Contact();
        contact.setId(dto.getId());
        contact.setName(dto.getName());
        contact.setEmail(dto.getEmail());
        contact.setPhone(dto.getPhone());
        contact.setSubject(dto.getSubject());
        contact.setMessage(dto.getMessage());
        contact.setIsRead(dto.getIsRead());
        contact.setCreatedAt(dto.getCreatedAt());
        return contact;
    }
}
