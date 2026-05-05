package com.merryblue.api.service;

import com.merryblue.api.dto.NotificationDTO;
import com.merryblue.api.model.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationDispatchService {

    private final EmailService emailService;
    private final NotificationService notificationService;
    private final SmsService smsService;

    public void dispatch(NotificationDTO notification, boolean urgent) {
        // High Logic: Route notification through channels based on priority
        notificationService.createNotification(notification);

        if (urgent || notification.getType() == NotificationType.ERROR) {
            emailService.sendSimpleMessage("admin@merryblue.com", 
                "URGENT: " + notification.getTitle(), 
                notification.getMessage());
            
            // If it's critical, send SMS (mock)
            if (urgent) {
                smsService.sendSms("+84123456789", "ALERT: " + notification.getTitle());
            }
        }
    }
}
