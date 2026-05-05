package com.merryblue.api.service;

public interface SmsService {
    void sendSms(String phoneNumber, String message);
}
