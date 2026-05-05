package com.merryblue.api.service;

import com.merryblue.api.dto.SiteContentDTO;
import com.merryblue.api.mapper.SiteContentMapper;
import com.merryblue.api.model.SiteContent;
import com.merryblue.api.repository.SiteContentRepository;
import com.merryblue.api.exception.ResourceNotFoundException;
import com.merryblue.api.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(value = "siteContent", key = "#key")
    public SiteContentDTO getContentByKey(String key) {
        SiteContent content = siteContentRepository.findByKey(key)
                .orElseThrow(() -> new BadRequestException("Content not found for key: " + key));
        return siteContentMapper.toDTO(content);
    }

    public SiteContent getEntityByKey(String key) {
        return siteContentRepository.findByKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found"));
    }

    @Transactional
    @CacheEvict(value = "siteContent", key = "#key")
    public SiteContentDTO updateContent(String key, SiteContentDTO updatedDTO) {
        SiteContent existing = getEntityByKey(key);
        existing.setValueVi(updatedDTO.getValueVi());
        existing.setValueEn(updatedDTO.getValueEn());
        existing.setDescription(updatedDTO.getDescription());
        return siteContentMapper.toDTO(siteContentRepository.save(existing));
    }
}
