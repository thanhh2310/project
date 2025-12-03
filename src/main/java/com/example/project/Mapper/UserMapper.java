package com.example.project.Mapper;

import com.example.project.Config.WebErrorConfig;
import com.example.project.DTO.Request.RegisterRequest;
import com.example.project.DTO.Request.UpdateProfileRequest;
import com.example.project.DTO.Request.UserCreationRequest;
import com.example.project.DTO.Request.UserUpdateRequest;
import com.example.project.DTO.Response.ProfileResponse;
import com.example.project.DTO.Response.UserResponse;
import com.example.project.Enum.ErrorCode;
import com.example.project.Enum.RoleName;
import com.example.project.Model.Role;
import com.example.project.Model.User;
import com.example.project.Repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public User registerToUser(RegisterRequest request){
        Role role = roleRepository.findByName(RoleName.ROLE_USER.name())
                .orElseThrow(() -> new WebErrorConfig(ErrorCode.ROLE_NOT_FOUND));
        return User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .roles(Set.of(role))
                .isActive(false)
                .build();
    }

    public ProfileResponse userToProfileResponse(User user){
        return ProfileResponse.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public void updateUserFromProfileRequest(User user, UpdateProfileRequest request){
        if(request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if(request.getLastName() != null) user.setLastName(request.getLastName());
        if(request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        // Lưu ý: Thường không cho user tự đổi email ở profile đơn giản vì liên quan đến login
    }

    public UserResponse fromUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles().stream()
                        .map(Role::getName) // Chỉ lấy tên Role
                        .collect(Collectors.toSet()))
                .build();
    }

    public User userCreationToUser(UserCreationRequest request){
        Set<Role> roles = request.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(()-> new WebErrorConfig(ErrorCode.ROLE_NOT_FOUND)))
                .collect(Collectors.toSet());
        return User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .isActive(request.getIsActive())
                .roles(roles)
                .build();
    }

    public void updateUserFromAdminRequest(User user, UserUpdateRequest request){
        if(request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if(request.getLastName() != null) user.setLastName(request.getLastName());
        if(request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if(request.getIsActive() != null) user.setIsActive(request.getIsActive());

        // Update Role nếu có
        if(request.getRoles() != null && !request.getRoles().isEmpty()){
            Set<Role> roles = request.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(()-> new WebErrorConfig(ErrorCode.ROLE_NOT_FOUND)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }
    }
}
