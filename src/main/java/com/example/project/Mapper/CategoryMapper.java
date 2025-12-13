package com.example.project.Mapper;

import com.example.project.Config.WebErrorConfig;
import com.example.project.DTO.Request.CategoryCreationRequest;
import com.example.project.DTO.Request.UpdateCategoryRequest;
import com.example.project.DTO.Response.CategoryResponse;
import com.example.project.Enum.ErrorCode;
import com.example.project.Model.Category;
import com.example.project.Repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    public CategoryResponse categoryToResponse(Category category, boolean includeChildren){
        CategoryResponse.CategoryResponseBuilder builder = CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .isActive(category.isActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt());
        if (category.getParent() != null) {
            builder.parentId(category.getParent().getId());
            builder.parentName(category.getParent().getName());
        }
        if(includeChildren && category.getChildren() != null){
            builder.children(category.getChildren().stream()
                    .map(child -> categoryToResponse(child, true))
                    .collect(Collectors.toList()));
        }else {
            builder.children(Collections.emptyList());
        }
        return builder.build();
    }

    public Category requestToCategory(CategoryCreationRequest request){
        return Category.builder()
                .name(request.getName())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                // Slug và Parent sẽ được set ở Service
                .build();
    }

    public void updateToCategory(Category category,UpdateCategoryRequest request){
        if(category.getName() != null){
            request.setName(category.getName());
        }
        if (category.isActive()) category.setActive(false);
        category.setActive(true);
    }



}
