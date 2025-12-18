package com.example.project.DTO.Response;

import com.example.project.Model.Attribute;
import com.example.project.Model.AttributeValue;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttributeResponse {
    Integer id;
    String name;
    String description;

    List<AttributeValueResponse> values;
}
