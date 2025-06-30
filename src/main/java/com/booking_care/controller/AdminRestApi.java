package com.booking_care.controller;

import com.booking_care.constant.ewewwewe.Status;
import com.booking_care.model.*;
import com.booking_care.repository.LichKhamRepository;
import com.booking_care.repository.TaiKhoanRepository;
import com.booking_care.security.CustomUserDetails;
import com.booking_care.security.JwtTokenProvider;
import com.booking_care.service.EmailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.mail.SendFailedException;
import javax.mail.internet.InternetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("/api")
public class AdminRestApi {

    private static final Logger logger = LoggerFactory.getLogger(AdminRestApi.class);

    @Autowired
    private TaiKhoanRepository taiKhoanRepo;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    @Qualifier("adminAuthenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Autowired
    @Qualifier("adminDetailsServiceImpl")
    private UserDetailsService adminDetailsService;

    @Autowired
    private LichKhamRepository lichKhamRepo;

    @Autowired
    private EmailSenderService emailSenderService;

    @PostMapping("/login/admin")
    public ResponseEntity<Map<String, Object>> loginAdmin(@RequestParam String username, @RequestParam String password) {
        logger.info("POST /api/login/admin - Nhận yêu cầu đăng nhập cho admin: username={}", username);
        return handleLogin(username, password, "ADMIN");
    }

    @RequestMapping("/login/admin")
    public ResponseEntity<Map<String, Object>> loginFallback() {
        logger.warn("Yêu cầu đến /api/login/admin không khớp với POST, trả về lỗi");
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Yêu cầu không hợp lệ, cần POST với username và password");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private ResponseEntity<Map<String, Object>> handleLogin(String username, String password, String requiredRole) {
        Map<String, Object> response = new HashMap<>();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = adminDetailsService.loadUserByUsername(username);
            String token = jwtTokenProvider.generateToken(authentication);

            TaiKhoan taiKhoan = taiKhoanRepo.findByUsername(username);
            if (taiKhoan == null || !taiKhoan.hasRole(requiredRole)) {
                response.put("error", "Tài khoản không phải " + requiredRole);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            response.put("token", token);
            response.put("username", username);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            logger.error("Đăng nhập thất bại cho username {}: {}", username, e.getMessage());
            response.put("error", "Tài khoản hoặc mật khẩu không đúng");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/admin/xacNhan")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public ResponseEntity<?> xacNhanLichKham(@AuthenticationPrincipal CustomUserDetails taiKhoan,
                                             @RequestParam("id") Integer id) {
        // Kiểm tra quyền truy cập
        if (taiKhoan == null || !taiKhoan.hasRole("ADMIN")) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Bạn không có quyền truy cập!");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        // Tìm lịch khám theo ID
        Optional<LichKham> optionalLichKham = lichKhamRepo.findById(id);
        if (!optionalLichKham.isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Lịch hẹn không tồn tại!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Kiểm tra trạng thái lịch khám
        LichKham lichKham = optionalLichKham.get();
        if (!lichKham.getStatus().equals(Status.CHO_XU_LY)) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Lịch hẹn không ở trạng thái 'Chờ xử lý'!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Lấy email bệnh nhân
        String email = lichKham.getBenhNhan() != null ? lichKham.getBenhNhan().getEmail() : null;
        logger.info("Admin xác nhận lịch khám ID: {}, Email bệnh nhân: {}", id, email);

        try {
            // Cập nhật trạng thái lịch khám
            lichKham.setStatus(Status.DA_XAC_NHAN);
            lichKhamRepo.save(lichKham);

            // Gửi email thông báo
            Email emailObj = new Email();
            emailObj.setTo(email);
            emailObj.setFrom(new InternetAddress("haibonglau411@gmail.com", "MEDICATE"));
            emailObj.setSubject("Thông tin chi tiết lịch khám");
            emailObj.setTemplate("template-email.html");
            Map<String, Object> properties = new HashMap<>();
            properties.put("lichKham", lichKham);
            emailObj.setProperties(properties);
            emailSenderService.sendHtmlMessage(emailObj);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Xác nhận lịch khám thành công!");
            return ResponseEntity.ok(response);
        } catch (SendFailedException e) {
            logger.error("Gửi email thất bại: {}", e.getMessage(), e);
            lichKham.setStatus(Status.CHO_XU_LY);
            lichKhamRepo.save(lichKham);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Gửi email thất bại!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            logger.error("Xác nhận lịch hẹn thất bại: {}", e.getMessage(), e);
            lichKham.setStatus(Status.CHO_XU_LY);
            lichKhamRepo.save(lichKham);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Xác nhận lịch hẹn thất bại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/admin/huyLichKham")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public ResponseEntity<?> huyLichKham(@AuthenticationPrincipal CustomUserDetails taiKhoan,
                                         @RequestParam("id") Integer id,
                                         @RequestParam("lyDoHuy") String lyDoHuy) {
        if (taiKhoan == null || !taiKhoan.hasRole("ADMIN")) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Bạn không có quyền truy cập!");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        Optional<LichKham> optionalLichKham = lichKhamRepo.findById(id);
        if (!optionalLichKham.isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Lịch hẹn không tồn tại!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        LichKham lichKham = optionalLichKham.get();
        if (!lichKham.getStatus().equals(Status.CHO_XU_LY)) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Chỉ có thể hủy lịch hẹn ở trạng thái 'Chờ xử lý'!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        String email = lichKham.getBenhNhan() != null ? lichKham.getBenhNhan().getEmail() : null;
        logger.info("Admin hủy lịch khám ID: {}, Email bệnh nhân: {}", id, email);

        if (lyDoHuy == null || lyDoHuy.trim().isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Vui lòng nhập lý do hủy!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (lyDoHuy.length() > 100) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Lý do hủy không được vượt quá 100 ký tự!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            lichKham.setStatus(Status.DA_HUY);
            lichKhamRepo.save(lichKham);

            Email emailObj = new Email();
            emailObj.setTo(email);
            emailObj.setFrom(new InternetAddress("haibonglau411@gmail.com", "MEDICATE"));
            emailObj.setSubject("Hủy lịch khám");
            emailObj.setTemplate("template-huy-lich.html");
            Map<String, Object> properties = new HashMap<>();
            properties.put("lichKham", lichKham);
            properties.put("lyDoHuy", lyDoHuy.trim());
            emailObj.setProperties(properties);
            emailSenderService.sendHtmlMessage(emailObj);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Hủy lịch khám thành công!");
            return ResponseEntity.ok(response);
        } catch (SendFailedException e) {
            logger.error("Gửi email thất bại: {}", e.getMessage(), e);
            lichKham.setStatus(Status.CHO_XU_LY);
            lichKhamRepo.save(lichKham);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Gửi email thất bại!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            logger.error("Hủy lịch hẹn thất bại: {}", e.getMessage(), e);
            lichKham.setStatus(Status.CHO_XU_LY);
            lichKhamRepo.save(lichKham);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Hủy lịch hẹn thất bại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}