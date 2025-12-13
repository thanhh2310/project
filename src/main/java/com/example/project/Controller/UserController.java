package com.example.project.Controller;

import com.example.project.DTO.Request.UpdateProfileRequest;
import com.example.project.DTO.Request.UserCreationRequest;
import com.example.project.DTO.Request.UserUpdateRequest;
import com.example.project.DTO.Response.ApiResponse;
import com.example.project.DTO.Response.PageResponse;
import com.example.project.DTO.Response.ProfileResponse;
import com.example.project.DTO.Response.UserResponse;
import com.example.project.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // api cho client
    @GetMapping("/my-profile")
    public ApiResponse<ProfileResponse> getMyProfile(){
        return ApiResponse.<ProfileResponse>builder()
                .code(200)
                .message("Get successfully")
                .data(userService.getMyProfile())
                .build();
    }

    @PutMapping("/update-profile")
    public ApiResponse<UserResponse> updateProfile(@RequestBody UpdateProfileRequest request){
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Update successfully")
                .data(userService.updateProfile(request))
                .build();
    }

    //------ api cho admin
    @GetMapping
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public ApiResponse<PageResponse<UserResponse>> getAllUser(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ApiResponse.<PageResponse<UserResponse>>builder()
                .code(200)
                .message("Get all users successfully")
                .data(userService.getAllUser(page, size))
                .build();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public ApiResponse<UserResponse> createUser(@RequestBody UserCreationRequest request){
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Create user successfully")
                .data(userService.createUser(request))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ApiResponse<UserResponse> updateUser(@RequestBody UserUpdateRequest request, @PathVariable Integer id){
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Update user successfully")
                .data(userService.updateUser(request, id))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public ApiResponse<Void> deleteUser(@PathVariable Integer id){
        userService.deleteUser(id);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("delete user successfully")
                .build();
    }
}
