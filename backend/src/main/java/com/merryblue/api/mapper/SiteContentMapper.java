package com.merryblue.api.mapper;

import com.merryblue.api.dto.SiteContentDTO;
import com.merryblue.api.model.SiteContent;
import org.springframework.stereotype.Component;

@Component
public class SiteContentMapper {

    public SiteContentDTO toDTO(SiteContent content) {
        if (content == null) return null;
        SiteContentDTO dto = new SiteContentDTO();
        dto.setId(content.getId());
        dto.setKey(content.getKey());
        dto.setValueVi(content.getValueVi());
        dto.setValueEn(content.getValueEn());
        dto.setDescription(content.getDescription());
        dto.setUpdatedAt(content.getUpdatedAt());
        return dto;
    }

    public SiteContent toEntity(SiteContentDTO dto) {
        if (dto == null) return null;
        SiteContent content = new SiteContent();
        content.setId(dto.getId());
        content.setKey(dto.getKey());
        content.setValueVi(dto.getValueVi());
        content.setValueEn(dto.getValueEn());
        content.setDescription(dto.getDescription());
        content.setUpdatedAt(dto.getUpdatedAt());
        return content;
    }
}
