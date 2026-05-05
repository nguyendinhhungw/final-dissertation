package com.merryblue.api.mapper;

import com.merryblue.api.dto.ServiceDTO;
import com.merryblue.api.model.Service;
import org.springframework.stereotype.Component;

@Component
public class BusinessServiceMapper {

    public ServiceDTO toDTO(Service service) {
        if (service == null) return null;
        ServiceDTO dto = new ServiceDTO();
        dto.setId(service.getId());
        dto.setSlug(service.getSlug());
        dto.setTitleVi(service.getTitleVi());
        dto.setTitleEn(service.getTitleEn());
        dto.setShortVi(service.getShortVi());
        dto.setShortEn(service.getShortEn());
        dto.setBodyVi(service.getBodyVi());
        dto.setBodyEn(service.getBodyEn());
        dto.setIcon(service.getIcon());
        dto.setImageUrl(service.getImageUrl());
        dto.setDisplayOrder(service.getDisplayOrder());
        dto.setIsPublished(service.getIsPublished());
        dto.setCreatedAt(service.getCreatedAt());
        return dto;
    }

    public Service toEntity(ServiceDTO dto) {
        if (dto == null) return null;
        Service service = new Service();
        service.setId(dto.getId());
        service.setSlug(dto.getSlug());
        service.setTitleVi(dto.getTitleVi());
        service.setTitleEn(dto.getTitleEn());
        service.setShortVi(dto.getShortVi());
        service.setShortEn(dto.getShortEn());
        service.setBodyVi(dto.getBodyVi());
        service.setBodyEn(dto.getBodyEn());
        service.setIcon(dto.getIcon());
        service.setImageUrl(dto.getImageUrl());
        service.setDisplayOrder(dto.getDisplayOrder());
        service.setIsPublished(dto.getIsPublished());
        service.setCreatedAt(dto.getCreatedAt());
        return service;
    }
}
