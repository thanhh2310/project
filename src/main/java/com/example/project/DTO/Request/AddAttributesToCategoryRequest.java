package com.example.project.DTO.Request;

import lombok.Data;

import java.util.List;

@Data
public class AddAttributesToCategoryRequest {
    List<CategoryAttributeRequest> attributes;
}
