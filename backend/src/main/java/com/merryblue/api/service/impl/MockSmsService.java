package com.merryblue.api.service.impl;

import com.merryblue.api.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MockSmsService implements SmsService {
    @Override
    public void sendSms(String phoneNumber, String message) {
        log.info("MOCK SMS SENT to: {}, message: {}", phoneNumber, message);
    }
}
