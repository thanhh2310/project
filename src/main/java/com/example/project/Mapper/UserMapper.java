package com.example.project.Mapper;

import com.example.project.Config.WebErrorConfig;
import com.example.project.DTO.Request.RegisterRequest;
import com.example.project.Enum.ErrorCode;
import com.example.project.Enum.RoleName;
import com.example.project.Model.Role;
import com.example.project.Model.User;
import com.example.project.Repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

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
}
