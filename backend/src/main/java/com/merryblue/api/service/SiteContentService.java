package com.merryblue.api.service;

import com.merryblue.api.dto.SiteContentDTO;
import com.merryblue.api.mapper.SiteContentMapper;
import com.merryblue.api.model.SiteContent;
import com.merryblue.api.repository.SiteContentRepository;
import com.merryblue.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SiteContentService {

    private final SiteContentRepository siteContentRepository;
    private final SiteContentMapper siteContentMapper;

    public List<SiteContentDTO> getAllContent() {
        return siteContentRepository.findAll()
                .stream().map(siteContentMapper::toDTO).collect(Collectors.toList());
    }

    public SiteContentDTO getContentByKey(String key) {
        return siteContentRepository.findByKey(key)
                .map(siteContentMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found"));
    }

    public SiteContent getEntityByKey(String key) {
        return siteContentRepository.findByKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found"));
    }

    @Transactional
    public SiteContentDTO updateContent(String key, SiteContentDTO updatedDTO) {
        SiteContent existing = getEntityByKey(key);
        existing.setValueVi(updatedDTO.getValueVi());
        existing.setValueEn(updatedDTO.getValueEn());
        existing.setDescription(updatedDTO.getDescription());
        return siteContentMapper.toDTO(siteContentRepository.save(existing));
    }
}
