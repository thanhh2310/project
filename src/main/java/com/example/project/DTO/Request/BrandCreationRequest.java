package com.example.project.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandCreationRequest {
    @NotBlank(message = "Brand name is required")
    @Size(min = 2, max = 100, message = "Brand name must be between 2 and 100 characters")
    String name;

    // Regex: Chỉ cho phép chữ thường (a-z), số (0-9) và dấu gạch ngang (-)
    @NotBlank(message = "Slug is required")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must contain only lowercase letters, numbers, and hyphens (e.g., nike-viet-nam)")
    String slug;

    @NotBlank(message = "Logo URL is required")
    @URL(message = "Logo URL must be a valid URL format") // Kiểm tra phải có http/https
    String logoUrl;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    String description;
}
