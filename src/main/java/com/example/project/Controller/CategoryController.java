package com.example.project.Controller;

import com.example.project.DTO.Request.CategoryCreationRequest;
import com.example.project.DTO.Request.UpdateCategoryRequest;
import com.example.project.DTO.Response.ApiResponse;
import com.example.project.DTO.Response.CategoryResponse;
import com.example.project.Service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PreAuthorize("hasAuthority('CATEGORY_MANAGE')")
    @PostMapping()
    public ApiResponse<CategoryResponse> createCategory(@RequestBody CategoryCreationRequest request){
        return ApiResponse.<CategoryResponse>builder()
                .code(200)
                .message("Create category successfully")
                .data(categoryService.createCategory(request))
                .build();
    }

    @PreAuthorize("hasAuthority('CATEGORY_MANAGE')")
    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> updateCategory(@RequestBody UpdateCategoryRequest request,
                                                        @PathVariable Integer id){
        return ApiResponse.<CategoryResponse>builder()
                .code(200)
                .message("Update successfully")
                .data(categoryService.updateCategory(request, id))
                .build();
    }

    @GetMapping()
    public ApiResponse<List<CategoryResponse>> getAllCategory(){
        return ApiResponse.<List<CategoryResponse>>builder()
                .code(200)
                .message("Get all successfully")
                .data(categoryService.getAllCategory())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable Integer id){
        return ApiResponse.<CategoryResponse>builder()
                .code(200)
                .message("this is detail category")
                .data(categoryService.getCategoryById(id))
                .build();
    }

    @PreAuthorize("hasAuthority('CATEGORY_MANAGE')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Integer id){
        categoryService.deleteCategory(id);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("delete successfully")
                .build();
    }
}
