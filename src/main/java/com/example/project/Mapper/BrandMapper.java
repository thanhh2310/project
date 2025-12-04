package com.example.project.Mapper;

import com.example.project.DTO.Request.BrandCreationRequest;
import com.example.project.DTO.Request.BrandUpdateRequest;
import com.example.project.DTO.Response.BrandResponse;
import com.example.project.Model.Brand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BrandMapper {
    public BrandResponse brandToBrandResponse(Brand brand){
        return BrandResponse.builder()
                .id(brand.getId())
                .slug(brand.getSlug())
                .name(brand.getName())
                .description(brand.getDescription())
                .logoUrl(brand.getLogoUrl())
                .isActive(brand.getIsActive())
                .createdAt(brand.getCreatedAt())
                .updatedAt(brand.getUpdatedAt())
                .build();
    }

    public Brand createToBrand(BrandCreationRequest request){
        return Brand.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .logoUrl(request.getLogoUrl())
                .description(request.getDescription())
                .isActive(true)
                .build();
    }

    public void updateBrandFromRequest(Brand brand, BrandUpdateRequest request) {
        if (request.getName() != null) brand.setName(request.getName());
        if (request.getSlug() != null) brand.setSlug(request.getSlug());
        if (request.getLogoUrl() != null) brand.setLogoUrl(request.getLogoUrl());
        if (request.getDescription() != null) brand.setDescription(request.getDescription());
        if (request.getIsActive() != null) brand.setIsActive(request.getIsActive());
    }
}
