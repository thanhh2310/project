package com.example.project.Config;

import com.example.project.Enum.ErrorCode;
import com.example.project.Enum.PermissionName;
import com.example.project.Enum.RoleName;
import com.example.project.Model.Permission;
import com.example.project.Model.Role;
import com.example.project.Model.User;
import com.example.project.Repository.PermissionRepository;
import com.example.project.Repository.RoleRepository;
import com.example.project.Repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitConfig {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    private void init(){
        initPermissions();
        initRoles();
        initAdminAccount();

    }

    private void initPermissions() {
        for (PermissionName permissionName : PermissionName.values()) {
            // Kiểm tra theo tên (slug)
            if (permissionRepository.findBySlug(permissionName.name()).isEmpty()) {
                Permission permission = new Permission();
                permission.setSlug(permissionName.name()); // Ví dụ: PRODUCT_CREATE
                permission.setDescription(permissionName.getDescription());
                permissionRepository.save(permission);
                log.info("INIT: Created permission {}", permissionName);
            }
        }
    }

    // Trong class InitConfig
    private void initRoles() {
        for (RoleName roleName : RoleName.values()) {
            if (roleRepository.findByName(roleName.name()).isEmpty()) {
                Role role = new Role();
                role.setName(roleName.name());
                role.setDescription("Vai tro la: " + roleName);

                Set<Permission> permissions = new HashSet<>();

                if (roleName == RoleName.ROLE_ADMIN) {
                    // 1. ADMIN: Lấy TẤT CẢ quyền
                    permissions.addAll(permissionRepository.findAll());
                }
                else if (roleName == RoleName.ROLE_STAFF) {
                    // 2. STAFF (Nhân viên vận hành): Lấy quyền Sản phẩm, Đơn hàng, Review, Coupon
                    // Dùng Stream filter để lấy nhanh các quyền liên quan
                    List<Permission> staffPerms = permissionRepository.findAll().stream()
                            .filter(p -> p.getSlug().startsWith("PRODUCT_")
                                    || p.getSlug().startsWith("ORDER_")
                                    || p.getSlug().startsWith("CATEGORY_")
                                    || p.getSlug().startsWith("BRAND_")
                                    || p.getSlug().startsWith("REVIEW_")
                                    || p.getSlug().startsWith("COUPON_"))
                            .collect(Collectors.toList());
                    permissions.addAll(staffPerms);
                }
                else if (roleName == RoleName.ROLE_USER) {
                    // 3. USER (Khách hàng): Thường quyền rất hạn chế hoặc để trống
                    // Ví dụ: Quyền được xem đơn hàng CỦA MÌNH (Logic này thường xử lý ở Code chứ ko phải DB permission)
                    // Nên thường để trống hoặc thêm quyền cơ bản.
                }

                role.setPermissions(permissions);
                roleRepository.save(role);
                log.info("INIT: Created role {} with {} permissions", roleName, permissions.size());
            }
        }
    }

    // 3. Khởi tạo tài khoản Admin
    private void initAdminAccount() {
        if (userRepository.findByEmail("admin@admin.com").isEmpty()) {
            Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN.name())
                    .orElseThrow(() -> new RuntimeException("Role Admin not found"));

            User user = User.builder()
                    .email("admin@admin.com")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .firstName("Super")
                    .lastName("Admin")
                    .isActive(true)
                    .roles(Set.of(adminRole))
                    .build();

            userRepository.save(user);
            log.info("INIT: Created Admin account");
        }
    }
}
