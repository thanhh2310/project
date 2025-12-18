package com.example.project.Controller;

import com.example.project.DTO.Request.AttributeCreationRequest;
import com.example.project.DTO.Request.UpdateAttributeRequest;
import com.example.project.DTO.Response.ApiResponse;
import com.example.project.DTO.Response.AttributeResponse;
import com.example.project.Service.AttributeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attributes")
@RequiredArgsConstructor
public class AttributeController {

    private final AttributeService attributeService;

    // --- PUBLIC API (Cho khách hàng xem bộ lọc) ---

    @GetMapping
    public ApiResponse<List<AttributeResponse>> getAllAttributes() {
        return ApiResponse.<List<AttributeResponse>>builder()
                .code(200)
                .message("Get all attributes successfully")
                .data(attributeService.getAllAttribute())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<AttributeResponse> getAttributeById(@PathVariable Integer id) {
        return ApiResponse.<AttributeResponse>builder()
                .code(200)
                .message("Get attribute detail successfully")
                .data(attributeService.getById(id))
                .build();
    }

    // --- ADMIN API (Cần quyền quản lý) ---

    @PostMapping
    @PreAuthorize("hasAuthority('ATTRIBUTE_MANAGE')")
    public ApiResponse<AttributeResponse> createAttribute(@RequestBody @Valid AttributeCreationRequest request) {
        return ApiResponse.<AttributeResponse>builder()
                .code(201) // 201 Created
                .message("Create attribute successfully")
                .data(attributeService.createAttribute(request))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ATTRIBUTE_MANAGE')")
    public ApiResponse<AttributeResponse> updateAttribute(@PathVariable Integer id,
                                                          @RequestBody @Valid UpdateAttributeRequest request) {
        return ApiResponse.<AttributeResponse>builder()
                .code(200)
                .message("Update attribute successfully")
                .data(attributeService.updateAttribute(request, id))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ATTRIBUTE_MANAGE')")
    public ApiResponse<Void> deleteAttribute(@PathVariable Integer id) {
        attributeService.deleteAttribute(id);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Delete attribute successfully")
                .build();
    }
}