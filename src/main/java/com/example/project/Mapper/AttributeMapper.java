package com.example.project.Mapper;

import com.example.project.DTO.Request.AttributeCreationRequest;
import com.example.project.DTO.Request.UpdateAttributeRequest;
import com.example.project.DTO.Response.AttributeResponse;
import com.example.project.DTO.Response.AttributeValueResponse;
import com.example.project.Model.Attribute;
import com.example.project.Model.AttributeValue;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AttributeMapper {
    public AttributeResponse attributeToResponse(Attribute attribute){
        List<AttributeValueResponse> values = new ArrayList<>();
        if(attribute.getValues() != null){
            values = attribute.getValues().stream()
                    .map(val -> AttributeValueResponse.builder()
                            .id(val.getId())
                            .value(val.getValue())
                            .build())
                    .collect(Collectors.toList());
        }

        return AttributeResponse.builder()
                .id(attribute.getId())
                .name(attribute.getName())
                .description(attribute.getDescription())
                .values(values)
                .build();
    }

    public Attribute requestToAttribute(AttributeCreationRequest request){
        return Attribute.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

    }

}
