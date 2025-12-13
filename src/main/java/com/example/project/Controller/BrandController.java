package com.example.project.Controller;

import com.example.project.DTO.Request.BrandCreationRequest;
import com.example.project.DTO.Request.BrandUpdateRequest;
import com.example.project.DTO.Response.ApiResponse;
import com.example.project.DTO.Response.BrandResponse;
import com.example.project.Service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    // --- PUBLIC API ---

    @GetMapping()
    public ApiResponse<List<BrandResponse>> getAllBrands() {
        return ApiResponse.<List<BrandResponse>>builder()
                .code(200)
                .message("Get all brands successfully")
                .data(brandService.getAllBrand())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<BrandResponse> getBrandById(@PathVariable Integer id) {
        return ApiResponse.<BrandResponse>builder()
                .code(200)
                .message("Get brand details successfully")
                .data(brandService.getBrandById(id))
                .build();
    }

    // --- ADMIN/STAFF API ---

    @PostMapping
    @PreAuthorize("hasAuthority('BRAND_MANAGE')")
    public ApiResponse<BrandResponse> createBrand(@RequestBody @Valid BrandCreationRequest request) {
        return ApiResponse.<BrandResponse>builder()
                .code(201) // 201 Created
                .message("Create brand successfully")
                .data(brandService.createBrand(request))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('BRAND_MANAGE')")
    public ApiResponse<BrandResponse> updateBrand(@PathVariable Integer id,
                                                  @RequestBody @Valid BrandUpdateRequest request) {
        return ApiResponse.<BrandResponse>builder()
                .code(200)
                .message("Update brand successfully")
                .data(brandService.updateBrand(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('BRAND_MANAGE')")
    public ApiResponse<Void> deleteBrand(@PathVariable Integer id) {
        brandService.deleteBrand(id);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Delete brand successfully")
                .build();
    }
}