package com.example.project.Service;

import com.example.project.Config.WebErrorConfig;
import com.example.project.DTO.Request.BrandCreationRequest;
import com.example.project.DTO.Request.BrandUpdateRequest;
import com.example.project.DTO.Response.BrandResponse;
import com.example.project.Enum.ErrorCode;
import com.example.project.Mapper.BrandMapper;
import com.example.project.Model.Brand;
import com.example.project.Repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    public List<BrandResponse> getAllBrand(){
        List<Brand> brands = brandRepository.findAll();
        return brands.stream()
                .map(brandMapper::brandToBrandResponse)
                .collect(Collectors.toList());
    }

    public BrandResponse getBrandById(Integer id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new WebErrorConfig(ErrorCode.BRAND_NOT_FOUND)); // Nhớ tạo ErrorCode này
        return brandMapper.brandToBrandResponse(brand);
    }

    @Transactional
    public BrandResponse createBrand(BrandCreationRequest request){
        if(brandRepository.findByName(request.getName()).isPresent()){
            throw new WebErrorConfig(ErrorCode.BRAND_ALREADY_EXISTS);
        }
        Brand brand = brandMapper.createToBrand(request);
        brandRepository.save(brand);
        return brandMapper.brandToBrandResponse(brand);
    }

    @Transactional
    public BrandResponse updateBrand(Integer id, BrandUpdateRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new WebErrorConfig(ErrorCode.BRAND_NOT_FOUND));

        if (request.getName() != null
                && !request.getName().equals(brand.getName())
                && brandRepository.existsByName(request.getName())) {
            throw new WebErrorConfig(ErrorCode.BRAND_ALREADY_EXISTS);
        }
        brandMapper.updateBrandFromRequest(brand, request);
        brandRepository.save(brand);

        return brandMapper.brandToBrandResponse(brand);
    }

    public void deleteBrand(Integer id) {
        if (!brandRepository.existsById(id)) {
            throw new WebErrorConfig(ErrorCode.BRAND_NOT_FOUND);
        }
        brandRepository.deleteById(id);
    }
}
