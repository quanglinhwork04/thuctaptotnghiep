package com.booking_care.controller;

import com.booking_care.constant.ewewwewe.Status;
import com.booking_care.model.BenhNhan;
import com.booking_care.model.TaiKhoan;
import com.booking_care.model.request.BookingRequest;
import com.booking_care.model.ChuyenKhoa;
import com.booking_care.model.LichKham;
import com.booking_care.repository.*;
import com.booking_care.security.CustomUserDetails;
import com.booking_care.security.JwtTokenProvider;
import com.booking_care.security.benh_nhan.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@RestController
@CrossOrigin(maxAge = 3600)
public class BenhNhanApi {

    private static final Logger logger = LoggerFactory.getLogger(BenhNhanApi.class);

    @Autowired
    private LichKhamRepository lichKhamRepo;

    @Autowired
    private BenhNhanRepository benhNhanRepo;

    @Autowired
    private ChuyenKhoaRepository chuyenKhoaRepo;

    @Autowired
    private BacSyRepository bacSyRepo;

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private TaiKhoanRepository taiKhoanRepo;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("/api/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String username, @RequestParam String password) {
        logger.info("POST /api/login - Nhận yêu cầu đăng nhập: username={}", username);
        return handleLogin(username, password);
    }

    // Endpoint fallback để debug
    @RequestMapping("/api/login")
    public ResponseEntity<Map<String, Object>> loginFallback() {
        logger.warn("Yêu cầu đến /api/login không khớp với POST, trả về lỗi");
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Yêu cầu không hợp lệ, cần POST với username và password");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private ResponseEntity<Map<String, Object>> handleLogin(String username, String password) {
        Map<String, Object> response = new HashMap<>();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String token = jwtTokenProvider.generateToken(authentication);

            // Lấy TaiKhoan từ repository
            TaiKhoan taiKhoan = taiKhoanRepo.findByUsername(username);
            if (taiKhoan == null) {
                logger.error("Tài khoản không tồn tại: {}", username);
                response.put("error", "Tài khoản không tồn tại");
                return ResponseEntity.status(404).body(response);
            }

            // Kiểm tra role BENH_NHAN
            if (!taiKhoan.hasRole("BENH_NHAN")) {
                logger.error("Tài khoản không có role BENH_NHAN: {}", username);
                response.put("error", "Tài khoản không phải bệnh nhân");
                return ResponseEntity.status(403).body(response);
            }

            logger.info("Đăng nhập thành công: username={}", username);
            response.put("token", token);
            response.put("username", username);
            response.put("redirect", "/");
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            logger.error("Đăng nhập thất bại: username={}. Lỗi: {}", username, e.getMessage());
            response.put("error", "Tài khoản hoặc mật khẩu không đúng");
            return ResponseEntity.status(401).body(response);
        } catch (Exception e) {
            logger.error("Lỗi không xác định khi đăng nhập: username={}. Lỗi: {}", username, e.getMessage());
            response.put("error", "Lỗi server");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/api/lichKhamBenh")
    @PreAuthorize("hasAuthority('BENH_NHAN')")
    public List<LichKham> getLichKhamBenhApi(@AuthenticationPrincipal CustomUserDetails taiKhoan,
                                             @RequestParam(value = "page", required = true, defaultValue = "1") Integer page,
                                             @RequestParam(value = "pageSize", required = true, defaultValue = "10") Integer pageSize,
                                             @RequestParam(value = "status", required = false) Integer status) {
        if (taiKhoan == null || !taiKhoan.hasRole("BENH_NHAN")) {
            return null;
        }
        BenhNhan benhNhan = benhNhanRepo.findByTaiKhoan(taiKhoan.getTaiKhoan());
        List<LichKham> lichKhamList = null;
        if (status != null) {
            if (status == 0) {
                lichKhamList = lichKhamRepo.getAllLichKhamOfBenhNhanByStatus(benhNhan.getId(), Status.CHO_XU_LY, PageRequest.of(page - 1, pageSize));
            }
            if (status == 1) {
                lichKhamList = lichKhamRepo.getAllLichKhamOfBenhNhanByStatus(benhNhan.getId(), Status.DA_XAC_NHAN, PageRequest.of(page - 1, pageSize));
            }
            if (status == 2) {
                lichKhamList = lichKhamRepo.getAllLichKhamOfBenhNhanByStatus(benhNhan.getId(), Status.DA_HUY, PageRequest.of(page - 1, pageSize));
            }
            if (status == 3) {
                lichKhamList = lichKhamRepo.getAllLichKhamOfBenhNhanByStatus(benhNhan.getId(), Status.DA_KHAM, PageRequest.of(page - 1, pageSize));
            }
        } else {
            lichKhamList = lichKhamRepo.getAllLichKhamOfBenhNhan(benhNhan.getId(), PageRequest.of(page - 1, pageSize));
        }
        return lichKhamList;
    }

    @PostMapping("/api/booking/dat-lich")
    @Transactional
    public ResponseEntity<Map<String, Object>> bookAppointment(
            @AuthenticationPrincipal CustomUserDetails taiKhoan,
            @Valid @RequestBody BookingRequest bookingRequest) throws ParseException {

        Map<String, Object> response = new HashMap<>();

        if (taiKhoan == null || !taiKhoan.hasRole("BENH_NHAN")) {
            response.put("error", "Vui lòng đăng nhập để đặt lịch");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        if (bookingRequest.getChuyenKhoaId() == null || bookingRequest.getChuyenKhoaId() == 0) {
            response.put("error", "Vui lòng chọn chuyên khoa");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (bookingRequest.getBacSyId() == null || bookingRequest.getBacSyId() == 0) {
            response.put("error", "Vui lòng chọn bác sĩ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (bookingRequest.getNgayKham() == null || bookingRequest.getNgayKham().isEmpty()) {
            response.put("error", "Vui lòng chọn ngày khám");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (bookingRequest.getMoTaTrieuChung().matches(".*[<>{}].*")) {
            response.put("error", "Mô tả triệu chứng không được chứa ký tự đặc biệt như <, >, {, }");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (bookingRequest.getKhungGioKham() == null || bookingRequest.getKhungGioKham() == 0 || bookingRequest.getKhungGioKham() > 12) {
            response.put("error", "Vui lòng chọn giờ khám hợp lệ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Date ngayKham;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            format.setLenient(false);
            ngayKham = format.parse(bookingRequest.getNgayKham());
        } catch (ParseException e) {
            response.put("error", "Định dạng ngày tháng không hợp lệ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (ngayKham.before(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))) {
            response.put("error", "Không thể đặt lịch cho ngày trong quá khứ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        List<LichKham> lichKhamList = lichKhamRepo.findByNgayKhamAndBacSyIdAndKhungGioKham(
                ngayKham, bookingRequest.getBacSyId(), bookingRequest.getKhungGioKham());
        if (!lichKhamList.isEmpty()) {
            response.put("error", "Giờ khám này đã được đặt. Vui lòng chọn giờ khác.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        if (bookingRequest.getMoTaTrieuChung() == null || bookingRequest.getMoTaTrieuChung().length() < 2) {
            response.put("error", "Mô tả triệu chứng phải có ít nhất 2 ký tự");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (bookingRequest.getMoTaTrieuChung().length() > 100) {
            response.put("error", "Mô tả triệu chứng không được vượt quá 100 ký tự");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        BenhNhan benhNhan = benhNhanRepo.findByTaiKhoan(taiKhoan.getTaiKhoan());
        if (benhNhan == null) {
            response.put("error", "Không tìm thấy thông tin bệnh nhân");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        LichKham lichKham = new LichKham();
        lichKham.setBenhNhan(benhNhan);
        lichKham.setBacSy(bacSyRepo.findById(bookingRequest.getBacSyId()).orElseThrow(() -> new IllegalArgumentException("Bác sĩ không tồn tại")));
        lichKham.setStatus(Status.CHO_XU_LY);
        lichKham.setKhungGioKham(bookingRequest.getKhungGioKham());
        lichKham.setChuyenKhoaId(bookingRequest.getChuyenKhoaId());
        lichKham.setMoTaTrieuChung(bookingRequest.getMoTaTrieuChung());
        lichKham.setNgayKham(ngayKham);
        lichKham.setTienKham(lichKham.getBacSy().getTienKham());
        lichKhamRepo.save(lichKham);

        response.put("success", "Đặt lịch thành công");
        return ResponseEntity.ok(response);
    }
}