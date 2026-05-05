package com.merryblue.api.service;

import com.merryblue.api.dto.ServiceDTO;
import com.merryblue.api.mapper.BusinessServiceMapper;
import com.merryblue.api.model.Service;
import com.merryblue.api.repository.ServiceRepository;
import com.merryblue.api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class BusinessServiceService {

    private final ServiceRepository serviceRepository;
    private final BusinessServiceMapper businessServiceMapper;

    public List<ServiceDTO> getAllServices(boolean onlyPublished) {
        List<Service> services;
        if (onlyPublished) {
            services = serviceRepository.findByIsPublishedTrueOrderByDisplayOrderAsc();
        } else {
            services = serviceRepository.findAllByOrderByDisplayOrderAsc();
        }
        return services.stream().map(businessServiceMapper::toDTO).collect(Collectors.toList());
    }

    public ServiceDTO getServiceBySlug(String slug) {
        return serviceRepository.findBySlug(slug)
                .map(businessServiceMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
    }

    public Service getServiceEntityById(UUID id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
    }

    @Transactional
    public ServiceDTO createService(ServiceDTO serviceDTO) {
        Service service = businessServiceMapper.toEntity(serviceDTO);
        return businessServiceMapper.toDTO(serviceRepository.save(service));
    }

    @Transactional
    public ServiceDTO updateService(UUID id, ServiceDTO updatedDTO) {
        Service existing = getServiceEntityById(id);
        Service updated = businessServiceMapper.toEntity(updatedDTO);
        updated.setId(existing.getId());
        updated.setCreatedAt(existing.getCreatedAt());
        return businessServiceMapper.toDTO(serviceRepository.save(updated));
    }

    @Transactional
    public void deleteService(UUID id) {
        serviceRepository.deleteById(id);
    }
}
