package com.merryblue.api.event;

import com.merryblue.api.dto.NotificationDTO;
import com.merryblue.api.service.EmailService;
import com.merryblue.api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobApplicationListener {

    private final EmailService emailService;
    private final NotificationService notificationService;

    @Async
    @EventListener
    public void handleJobApplicationSubmitted(JobApplicationSubmittedEvent event) {
        log.info("Handling JobApplicationSubmittedEvent for application ID: {}", event.getApplication().getId());
        
        // 1. Send Email to applicant
        emailService.sendSimpleMessage(
            event.getApplication().getEmail(),
            "Application Received",
            "Dear " + event.getApplication().getFullName() + ", we have received your application for " + event.getApplication().getJob().getTitleVi()
        );
        
        // 2. Create In-app notification
        NotificationDTO notification = new NotificationDTO();
        notification.setUserId(event.getApplication().getUserId());
        notification.setTitle("Application Submitted");
        notification.setMessage("You have successfully applied for " + event.getApplication().getJob().getTitleVi());
        notification.setIsRead(false);
        notificationService.createNotification(notification);
    }
}
