package com.example.project.Service;

import com.example.project.Config.WebErrorConfig;
import com.example.project.DTO.Request.UpdateProfileRequest;
import com.example.project.DTO.Request.UserCreationRequest;
import com.example.project.DTO.Request.UserUpdateRequest;
import com.example.project.DTO.Response.PageResponse;
import com.example.project.DTO.Response.ProfileResponse;
import com.example.project.DTO.Response.UserResponse;
import com.example.project.Enum.ErrorCode;
import com.example.project.Mapper.UserMapper;
import com.example.project.Model.User;
import com.example.project.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // SỬA LẠI TÌM BẰNG EMAIL

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new WebErrorConfig(ErrorCode.USER_NOT_FOUND));
    }

    //------- api cho user
    public ProfileResponse getMyProfile(){
        // lay email tu nguoi dung dang đăng nhập
        var authenticate = SecurityContextHolder.getContext().getAuthentication();
        String email = authenticate.getName();

        // tim nguoi dung bang email
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new WebErrorConfig(ErrorCode.USER_NOT_FOUND));

        return userMapper.userToProfileResponse(user);
    }

    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request){
        // lay email tu nguoi dung dang đăng nhập
        var authenticate = SecurityContextHolder.getContext().getAuthentication();
        String email = authenticate.getName();

        System.out.println("EMAIL: " + email);

        // tim nguoi dung bang email
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new WebErrorConfig(ErrorCode.USER_NOT_FOUND));

        userMapper.updateUserFromProfileRequest(user,request);
        userRepository.save(user);

        return userMapper.fromUser(user);
    }

    //----- api cho admin
    public PageResponse<UserResponse> getAllUser(int pageNumber, int pageSize ){
        // Tạo Pageable
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize,
                Sort.by("createdAt").descending());

        // Query DB (Tự động tính limit/offset)
        Page<User> pageData = userRepository.findAll(pageable);

        // Map từ Entity sang Response DTO
        List<UserResponse> userResponses = pageData.getContent().stream()
                .map(userMapper::fromUser)
                .collect(Collectors.toList());

        return PageResponse.<UserResponse>builder()
                .currentPage(pageNumber)
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPage(pageData.getTotalPages())
                .data(userResponses)
                .build();
    }

    @Transactional
    public UserResponse createUser(UserCreationRequest request){
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new WebErrorConfig(ErrorCode.USER_AlREADY_EXISTED);
        }
        User user = userMapper.userCreationToUser(request);
        userRepository.save(user);
        return userMapper.fromUser(user);
    }

    @Transactional
    public UserResponse updateUser(UserUpdateRequest request, Integer id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new WebErrorConfig(ErrorCode.USER_NOT_FOUND));
        userMapper.updateUserFromAdminRequest(user, request);
        userRepository.save(user);
        return userMapper.fromUser(user);
    }

    public void deleteUser(Integer id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new WebErrorConfig(ErrorCode.USER_NOT_FOUND));
        userRepository.delete(user);
    }
}
