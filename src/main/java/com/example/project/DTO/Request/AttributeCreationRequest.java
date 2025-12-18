package com.example.project.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttributeCreationRequest {
    @NotBlank(message = "Attribute name is required")
    String name;

    String description;

    // tao luon values neu can
    List<String> values;

}
