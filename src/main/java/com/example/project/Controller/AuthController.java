package com.example.project.Controller;

import com.example.project.DTO.Request.*;
import com.example.project.DTO.Response.ApiResponse;
import com.example.project.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody @Valid RegisterRequest request){
        authService.register(request);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Register successfully")
                .build();
    }

    @PostMapping("/verify")
    public ApiResponse<Void> verify(@RequestBody @Valid VerifyRequest request){
        authService.verify(request);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Verify successfully")
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<Void> login(@RequestBody @Valid LoginRequest request){
        authService.login(request);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Login successfully")
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody @Valid LogoutRequest request,
                                    @RequestHeader("Authorization") String authHeader){
        authService.logout( authHeader,request);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Logout successfully")
                .build();
    }

    // --- FORGOT PASSWORD ---
    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request){
        authService.forgotPassword(request);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("OTP has been sent to your email.")
                .build();
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request){
        authService.resetPassword(request);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Reset password successfully")
                .build();
    }

    // --- CHANGE PASSWORD ---
    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request){
        authService.changePassword(request);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Change password successfully")
                .build();
    }
}
