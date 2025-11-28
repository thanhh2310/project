package com.example.project.Config;

import com.example.project.Enum.ErrorCode;
import com.example.project.Enum.RoleName;
import com.example.project.Model.Role;
import com.example.project.Model.User;
import com.example.project.Repository.RoleRepository;
import com.example.project.Repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class InitConfig {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    private void init(){
        // khoi tao Role
        for(RoleName roleName : RoleName.values()){
            if (roleRepository.findByName(roleName.name()).isEmpty()){
                Role role = new Role();
                role.setName(roleName.name());
                role.setDescription("Vai tro la: " + roleName);
                roleRepository.save(role);
            }else {
                throw new WebErrorConfig(ErrorCode.ROLE_ALREADY_EXISTED);
            }
        }

        // khoi tao tai khoan admin
        if(userRepository.findByEmail("admin@admin.com").isEmpty()){
            User user = new User();
            user.setEmail("admin@admin.com");
            user.setPasswordHash(passwordEncoder.encode("admin123"));
            Role role = roleRepository.findByName(RoleName.ROLE_ADMIN.name())
                            .orElseThrow(() -> new WebErrorConfig(ErrorCode.ROLE_NOT_FOUND));
            user.setRoles(Set.of(role));
            userRepository.save(user);
        }
    }
}
