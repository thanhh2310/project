package com.example.project.DTO.Request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogoutRequest {
    private String refreshToken;
}
