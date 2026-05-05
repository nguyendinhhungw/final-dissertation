package com.merryblue.api.mapper;

import com.merryblue.api.dto.NotificationDTO;
import com.merryblue.api.model.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationDTO toDTO(Notification notification) {
        if (notification == null) return null;
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUserId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setLink(notification.getLink());
        dto.setIsRead(notification.getIsRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }

    public Notification toEntity(NotificationDTO dto) {
        if (dto == null) return null;
        Notification notification = new Notification();
        notification.setId(dto.getId());
        notification.setUserId(dto.getUserId());
        notification.setTitle(dto.getTitle());
        notification.setMessage(dto.getMessage());
        notification.setLink(dto.getLink());
        notification.setIsRead(dto.getIsRead());
        notification.setCreatedAt(dto.getCreatedAt());
        return notification;
    }
}
