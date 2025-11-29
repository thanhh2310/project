package com.example.project.Service;

import com.example.project.Auth.JwtUtils;
import com.example.project.Config.WebErrorConfig;
import com.example.project.DTO.Request.*;
import com.example.project.DTO.Response.TokenResponse;
import com.example.project.Enum.ErrorCode;
import com.example.project.Mapper.UserMapper;
import com.example.project.Model.Cart;
import com.example.project.Model.User;
import com.example.project.Repository.CartRepository;
import com.example.project.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CartRepository cartRepository;
    private final  EmailService emailService;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RedisService redisService;

    // ham register
    @Transactional
    public void register(RegisterRequest request){
        // kiem tra xem user ton tai chua
        if(userRepository.existsByEmail(request.getEmail())){
            throw new WebErrorConfig(ErrorCode.USER_AlREADY_EXISTED);
        }
        //chuyen du lieu qua user va save vao db
        User user = userMapper.registerToUser(request);
        userRepository.save(user);

        // tao gio hang rong cho User moi
        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);

        //tao va gui otp qua email
        String code = otpService.generateAndStoreOtp(request.getEmail());
        emailService.sendVerificationCode(request.getEmail(), code);
    }

    // xac thuc tai khoan
    @Transactional
    public void verify(VerifyRequest request){
        // Xac thuc tai khoan
        if(!otpService.validateOtp(request.getEmail(), request.getCode())){
            throw new WebErrorConfig(ErrorCode.INVALID_OTP_CODE);
        }

        // tim nguoi dung roi kich hoat tai khoan
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new WebErrorConfig(ErrorCode.USER_NOT_FOUND));
        user.setIsActive(true);

        //luu thong tin nguoi dug lai
        userRepository.save(user);
    }

    public TokenResponse login(LoginRequest request) {
        try {
            // 1. AuthenticationManager sẽ tự động làm các việc:
            //    - Tìm User theo email
            //    - Check Password (bằng passwordEncoder.matches)
            //    - Check User có Active không (qua hàm isEnabled() trong Entity User)
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // 2. Nếu chạy đến dòng này nghĩa là đăng nhập thành công
            // Set thông tin vào Context (để dùng cho các filter phía sau nếu cần)
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Lấy User ra (Principal chính là User entity của bạn vì class User implement UserDetails)
            User userAuth = (User) auth.getPrincipal();

            // 3. Sinh Token
            String accessToken = jwtUtils.generateAccessToken(userAuth);
            String refreshToken = jwtUtils.generateRefreshToken(userAuth);

            // 4. Lưu Refresh Token vào Redis (Cache)
            // Redis nên lưu key theo format: "refresh_token:{username}" để dễ quản lý
            redisService.saveRefreshTokenToRedis(refreshToken, jwtUtils.getJwtLongExpiration());

            return TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

        } catch (DisabledException e) {
            // Bắt lỗi riêng user chưa kích hoạt (Active = false)
            throw new WebErrorConfig(ErrorCode.USER_NOT_ACTIVE);

        } catch (BadCredentialsException e) {
            // Bắt lỗi sai email hoặc pass
            throw new WebErrorConfig(ErrorCode.EMAIL_OR_PASSWORD_NOT_CORRECT);

        } catch (AuthenticationException e) {
            // Các lỗi khác của Spring Security
            throw new WebErrorConfig(ErrorCode.UNAUTHENTICATED);
        }
    }

    public void logout(String authHeader, LogoutRequest request){
        // 1. Xử lý Access Token (Blacklist)
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);

            // Lấy thời gian hết hạn để tính TTL
            Date expirationDate = jwtUtils.extractExpiration(accessToken);
            long nowMillis = System.currentTimeMillis();
            long ttlMillis = expirationDate.getTime() - nowMillis;

            // Chỉ blacklist nếu token còn sống
            if (ttlMillis > 0) {
                redisService.blackListToken(accessToken, ttlMillis);
            }
        }

        // 2. Xử lý Refresh Token (Xóa khỏi Redis) - QUAN TRỌNG
        // Nếu không có bước này, Logout coi như vô nghĩa
        if (request != null && StringUtils.hasText(request.getRefreshToken())) {
            redisService.deleteRefreshToken(request.getRefreshToken());
        }
    }

    // gui yeu cau quen mat khau
    public void forgotPassword(ForgotPasswordRequest request){
        // kiem tra xem nguoi dung co ton tai khong
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new WebErrorConfig(ErrorCode.USER_NOT_FOUND));

        //sinh otp va luu vao redis
        String code = otpService.generateAndStoreOtp(request.getEmail());

        // gui email
        emailService.sendResetPasswordOtp(user.getEmail(), code);
    }

    //thuc hien doi mat khau bang otp
    @Transactional
    public void resetPassword(ResetPasswordRequest request){
        // Xac thuc tai khoan
        if(!otpService.validateOtp(request.getEmail(), request.getOtp())){
            throw new WebErrorConfig(ErrorCode.INVALID_OTP_CODE);
        }

        // lay user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new WebErrorConfig(ErrorCode.USER_NOT_FOUND));

        //Update mat khau moi
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request){
        // Lấy User hiện tại từ SecurityContext (Người đang đăng nhập)
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail =  authentication.getName();

        System.out.println("DEBUG: Authentication Name is: " + currentEmail);

        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new WebErrorConfig(ErrorCode.USER_NOT_FOUND));

        // Check mật khẩu cũ có đúng không
        if(!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())){
            throw new WebErrorConfig(ErrorCode.PASSWORD_NOT_CORRECT);
        }
        // Check mật khẩu mới và confirm có khớp nhau không
        if(!request.getNewPassword().equals(request.getConfirmationPassword())){
            throw new WebErrorConfig(ErrorCode.PASSWORD_NOT_MATCH);
        }
        // Lưu mật khẩu mới
        currentUser.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
    }
}
