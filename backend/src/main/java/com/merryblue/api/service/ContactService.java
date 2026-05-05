package com.merryblue.api.service;

import com.merryblue.api.dto.ContactDTO;
import com.merryblue.api.mapper.ContactMapper;
import com.merryblue.api.model.Contact;
import com.merryblue.api.repository.ContactRepository;
import com.merryblue.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;

    public List<ContactDTO> getAllContacts() {
        return contactRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(contactMapper::toDTO).collect(Collectors.toList());
    }

    public Contact getContactEntityById(UUID id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
    }

    @Transactional
    public ContactDTO createContact(ContactDTO contactDTO) {
        Contact contact = contactMapper.toEntity(contactDTO);
        return contactMapper.toDTO(contactRepository.save(contact));
    }

    @Transactional
    public ContactDTO markAsRead(UUID id) {
        Contact contact = getContactEntityById(id);
        contact.setIsRead(true);
        return contactMapper.toDTO(contactRepository.save(contact));
    }

    @Transactional
    public void deleteContact(UUID id) {
        contactRepository.deleteById(id);
    }
}
