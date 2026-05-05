package com.merryblue.api.service.impl;

import com.merryblue.api.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MockEmailService implements EmailService {

    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        log.info("MOCK EMAIL SENT to: {}, subject: {}, text: {}", to, subject, text);
    }

    @Override
    public void sendHtmlMessage(String to, String subject, String htmlBody) {
        log.info("MOCK HTML EMAIL SENT to: {}, subject: {}", to, subject);
    }
}
