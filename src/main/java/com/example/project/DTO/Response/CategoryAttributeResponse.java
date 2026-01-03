package com.example.project.DTO.Response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryAttributeResponse {
    Integer attributeId;
    String attributeName;
    Boolean isRequired;
    Boolean isFilterable;
}
