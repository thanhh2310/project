package com.example.project.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryCreationRequest {
    @NotBlank(message = "Category name is required")
    String name;
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug format is invalid")
    String slug;

    //Nếu null -> Đây là danh mục gốc (Root)
    // Nếu có giá trị -> Đây là danh mục con (Sub)
    Integer parentId;
    Boolean isActive = true;
}
