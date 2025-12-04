package com.example.project.DTO.Request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrandUpdateRequest {

    @Size(min = 2, max = 100, message = "Brand name must be between 2 and 100 characters")
    String name;

    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must contain only lowercase letters, numbers, and hyphens")
    String slug;

    @URL(message = "Logo URL must be a valid URL format")
    String logoUrl;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    String description;

    // Admin có quyền ẩn/hiện thương hiệu
    Boolean isActive;
}