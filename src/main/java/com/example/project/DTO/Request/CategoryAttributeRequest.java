package com.example.project.DTO.Request;

import lombok.Data;

@Data
public class CategoryAttributeRequest {
    Integer attributeId;
    Boolean isRequired = false;
    Boolean isFilterable = false;
}
