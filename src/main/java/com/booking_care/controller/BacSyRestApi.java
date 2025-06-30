package com.booking_care.controller;

import com.booking_care.constant.ewewwewe.Status;
import com.booking_care.model.*;
import com.booking_care.model.request.ToaThuocRequest;
import com.booking_care.repository.*;
import com.booking_care.security.CustomUserDetails;
import com.booking_care.security.JwtTokenProvider;
import com.booking_care.service.EmailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.mail.SendFailedException;
import javax.mail.internet.InternetAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("/api")
public class BacSyRestApi {

    private static final Logger logger = LoggerFactory.getLogger(BacSyRestApi.class);

    @Autowired
    private BacSyRepository bacSyRepo;

    @Autowired
    private ChuyenKhoaRepository chuyenKhoaRepo;

    @Autowired
    private LichKhamRepository lichKhamRepo;

    @Autowired
    private ThuocRepository thuocRepo;

    @Autowired
    private ToaThuocRepository toaThuocRepo;

    @Autowired
    private ChiTietToaThuocRepository chiTietToaThuocRepo;

    @Autowired
    private LichKhamBacSyRepository lichKhamBacSyRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private TaiKhoanRepository taiKhoanRepo;

    @Autowired
    @Qualifier("bacSyAuthenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Autowired
    @Qualifier("bacSyUserDetailsService")
    private UserDetailsService userDetailsService;

    @PostMapping("/login/bac-sy")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String username, @RequestParam String password) {
        logger.info("POST /api/login/bac-sy - Nhận yêu cầu đăng nhập cho bác sĩ: username={}", username);
        return handleLogin(username, password, "BAC_SY");
    }

    @RequestMapping("/login/bac-sy")
    public ResponseEntity<Map<String, Object>> loginFallback() {
        logger.warn("Yêu cầu đến /api/login không khớp với POST, trả về lỗi");
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
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
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

    @GetMapping("/getAllBacSy/chuyenKhoa/{idChuyenKhoa}")
    public ResponseEntity<?> getAllBacSyByChuyenKhoa(@PathVariable("idChuyenKhoa") Integer idChuyenKhoa) {
        try {
            if (idChuyenKhoa == null || idChuyenKhoa <= 0) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "ID chuyên khoa không hợp lệ.");
                logger.warn("ID chuyên khoa không hợp lệ: {}", idChuyenKhoa);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (!chuyenKhoaRepo.existsById(idChuyenKhoa)) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Chuyên khoa không tồn tại.");
                logger.warn("Chuyên khoa không tồn tại với ID: {}", idChuyenKhoa);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            List<BacSy> bacSyList = bacSyRepo.findByChuyenKhoaIdAndTaiKhoanVaiTroTen(idChuyenKhoa, "BAC_SY");
            if (bacSyList.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Không tìm thấy bác sĩ nào thuộc chuyên khoa này.");
                logger.info("Không có bác sĩ nào cho chuyên khoa ID: {}", idChuyenKhoa);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            logger.info("Lấy thành công {} bác sĩ cho chuyên khoa ID: {}", bacSyList.size(), idChuyenKhoa);
            return ResponseEntity.ok(bacSyList);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Lỗi hệ thống khi lấy danh sách bác sĩ: " + e.getMessage());
            logger.error("Lỗi khi lấy danh sách bác sĩ cho chuyên khoa ID {}: {}", idChuyenKhoa, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/bacsy/{id}")
    public ResponseEntity<BacSy> getBacSyApi(@PathVariable Integer id) {
        try {
            BacSy bacSy = bacSyRepo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Bác sĩ không tồn tại."));
            return ResponseEntity.ok(bacSy);
        } catch (IllegalArgumentException e) {
            logger.error("Bác sĩ không tồn tại: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy thông tin bác sĩ: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/chuyen-khoa")
    public ResponseEntity<List<ChuyenKhoa>> getAllChuyenKhoa() {
        try {
            List<ChuyenKhoa> chuyenKhoaList = chuyenKhoaRepo.findAll();
            return ResponseEntity.ok(chuyenKhoaList);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách chuyên khoa: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/bacsy/search")
    public ResponseEntity<Map<String, Object>> searchDoctors(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "chuyenKhoaId", required = false) String chuyenKhoaId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        logger.info("Received search request - keyword: {}, chuyenKhoaId: {}, page: {}, pageSize: {}",
                keyword, chuyenKhoaId, page, pageSize);

        try {
            if (keyword != null && keyword.trim().length() > 50) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Từ khóa tìm kiếm quá dài (tối đa 50 ký tự).");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (keyword != null && keyword.matches(".*[<>{}();\"'`].*")) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Từ khóa chứa ký tự không hợp lệ (ví dụ: <, >, ;, ').");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Integer chuyenKhoaIdInt = null;
            if (chuyenKhoaId != null && !chuyenKhoaId.trim().isEmpty()) {
                try {
                    chuyenKhoaIdInt = Integer.parseInt(chuyenKhoaId);
                    if (!chuyenKhoaRepo.existsById(chuyenKhoaIdInt)) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("error", "Chuyên khoa không tồn tại.");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                    }
                } catch (NumberFormatException e) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("error", "ID chuyên khoa không hợp lệ.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            }

            Page<BacSy> bacSyPage;
            if (keyword != null && !keyword.trim().isEmpty() && chuyenKhoaIdInt != null) {
                bacSyPage = bacSyRepo.findByKeywordAndChuyenKhoa(keyword.trim(), chuyenKhoaIdInt, PageRequest.of(page - 1, pageSize));
            } else if (keyword != null && !keyword.trim().isEmpty()) {
                bacSyPage = bacSyRepo.findByKeyword(keyword.trim(), PageRequest.of(page - 1, pageSize));
            } else if (chuyenKhoaIdInt != null) {
                bacSyPage = bacSyRepo.findByChuyenKhoaId(chuyenKhoaIdInt, PageRequest.of(page - 1, pageSize));
            } else {
                bacSyPage = bacSyRepo.findAll(PageRequest.of(page - 1, pageSize));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("bacSyList", bacSyPage.getContent());
            response.put("currentPage", bacSyPage.getNumber() + 1);
            response.put("totalPages", bacSyPage.getTotalPages());
            response.put("totalItems", bacSyPage.getTotalElements());

            if (bacSyPage.getContent().isEmpty()) {
                response.put("message", "Không tìm thấy bác sĩ phù hợp với tiêu chí!");
            } else if (bacSyPage.getContent().stream().anyMatch(bacSy -> bacSy.getId() == null)) {
                response.put("message", "Dữ liệu bác sĩ không hợp lệ (ID null).");
            }

            if (bacSyPage.getTotalPages() > 0) {
                List<Integer> pageNumbers = IntStream.rangeClosed(1, bacSyPage.getTotalPages())
                        .boxed()
                        .collect(Collectors.toList());
                response.put("pageNumbers", pageNumbers);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Lỗi hệ thống: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Lỗi hệ thống: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/bacsy/danhSachThuoc")
    @PreAuthorize("hasAuthority('BAC_SY')")
    public ResponseEntity<?> getAllThuoc(@AuthenticationPrincipal CustomUserDetails taiKhoan) {
        if (taiKhoan == null || !taiKhoan.hasRole("BAC_SY")) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Bạn không có quyền truy cập!");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            List<Thuoc> thuocList = thuocRepo.findAll();
            if (thuocList.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Danh sách thuốc trống!");
                response.put("thuocList", thuocList);
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.ok(thuocList);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Lỗi khi lấy danh sách thuốc: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/bacsy/guiToaThuoc")
    @PreAuthorize("hasAuthority('BAC_SY')")
    @Transactional
    public ResponseEntity<?> guiToaThuoc(@AuthenticationPrincipal CustomUserDetails taiKhoan,
                                         @RequestBody ToaThuocRequest request) {
        if (taiKhoan == null || !taiKhoan.hasRole("BAC_SY")) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Bạn không có quyền truy cập!");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            if (request.getLichKhamId() == null || request.getLichKhamId().trim().isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "ID lịch khám không được để trống");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            int lichKhamId;
            try {
                lichKhamId = Integer.parseInt(request.getLichKhamId());
            } catch (NumberFormatException e) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "ID lịch khám không hợp lệ: " + request.getLichKhamId());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Optional<LichKham> optionalLichKham = lichKhamRepo.findById(lichKhamId);
            if (!optionalLichKham.isPresent()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Lịch khám không tồn tại");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            LichKham lichKham = optionalLichKham.get();
            if (!lichKham.getStatus().equals(Status.DA_XAC_NHAN)) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Lịch hẹn phải ở trạng thái 'Đã xác nhận' để gửi toa thuốc");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            String email = lichKham.getBenhNhan() != null ? lichKham.getBenhNhan().getEmail() : null;
            logger.info("Gửi toa thuốc cho lịch khám ID: {}, Email bệnh nhân: {}", lichKhamId, email);

            if (request.getChanDoan() == null || request.getChanDoan().trim().isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Chuẩn đoán không được để trống");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (request.getThuocList() == null || request.getThuocList().isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Danh sách thuốc không được để trống");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            lichKham.setChanDoan(request.getChanDoan());
            lichKhamRepo.save(lichKham);

            ToaThuoc toaThuoc = new ToaThuoc();
            toaThuoc.setLichKham(lichKham);
            toaThuoc.setBenhNhan(lichKham.getBenhNhan());
            toaThuocRepo.save(toaThuoc);

            List<Map<String, Object>> thuocItems = new ArrayList<>();
            for (ToaThuocRequest.ThuocItem item : request.getThuocList()) {
                if (item.getThuocId() == null) {
                    Map<String, String> response = new HashMap<>();
                    response.put("error", "ID thuốc không được để trống");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
                Thuoc thuoc = thuocRepo.findById(item.getThuocId())
                        .orElseThrow(() -> new IllegalArgumentException("Thuốc không tồn tại: " + item.getThuocId()));

                ChiTietToaThuoc chiTiet = new ChiTietToaThuoc();
                chiTiet.setToaThuoc(toaThuoc);
                chiTiet.setThuoc(thuoc);
                chiTiet.setDonViTinh(item.getDonViTinh() != null ? item.getDonViTinh() : "");
                chiTiet.setSoLuong(item.getSoLuong());
                chiTiet.setHdsd(item.getHdsd() != null ? item.getHdsd() : "");
                chiTietToaThuocRepo.save(chiTiet);

                Map<String, Object> thuocData = new HashMap<>();
                thuocData.put("tenThuoc", thuoc.getTenThuoc());
                thuocData.put("donViTinh", item.getDonViTinh());
                thuocData.put("soLuong", item.getSoLuong());
                thuocData.put("hdsd", item.getHdsd());
                thuocItems.add(thuocData);
            }

            Email emailObj = new Email();
            emailObj.setTo(email);
            emailObj.setFrom(new InternetAddress("haibonglau411@gmail.com", "MEDICATE"));
            emailObj.setSubject("Toa thuốc từ bác sĩ " + lichKham.getBacSy().getHoTen());
            emailObj.setTemplate("template-toa-thuoc.html");
            Map<String, Object> properties = new HashMap<>();
            properties.put("benhNhan", lichKham.getBenhNhan());
            properties.put("bacSy", lichKham.getBacSy());
            properties.put("thuocItems", thuocItems);
            properties.put("chanDoan", lichKham.getChanDoan());
            emailObj.setProperties(properties);
            emailSenderService.sendHtmlMessage(emailObj);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Toa thuốc đã được gửi và lưu thành công!");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Gửi toa thuốc thất bại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Gửi toa thuốc thất bại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email != null && !email.trim().isEmpty() && email.length() <= 100 && email.matches(emailRegex);
    }

    @PostMapping("/bacsy/daKham")
    @PreAuthorize("hasAuthority('BAC_SY')")
    @Transactional
    public ResponseEntity<Map<String, String>> markAsCompleted(
            @AuthenticationPrincipal CustomUserDetails taiKhoan,
            @RequestParam("id") Integer id) {
        logger.info("Processing daKham request for ID: {}", id);

        Map<String, String> response = new HashMap<>();

        if (taiKhoan == null || !taiKhoan.hasRole("BAC_SY")) {
            response.put("error", "Bạn không có quyền truy cập!");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        Optional<LichKham> optionalLichKham = lichKhamRepo.findById(id);
        if (!optionalLichKham.isPresent()) {
            response.put("error", "Lịch hẹn không có tồn tại!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        LichKham lichKham = optionalLichKham.get();
        if (!lichKham.getStatus().equals(Status.DA_XAC_NHAN)) {
            response.put("error", "Chỉ có thể đánh dấu 'Đã khám' cho lịch hẹn ở trạng thái 'Đã xác nhận'!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            lichKham.setStatus(Status.DA_KHAM);
            lichKhamRepo.save(lichKham);
            response.put("message", "Chuyển trạng thái 'Đã khám' thành công!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Đánh dấu đã khám thất bại: {}", e.getMessage(), e);
            response.put("error", "Đánh dấu đã khám thất bại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/bacsy/dangKyLichKham")
    @PreAuthorize("hasAuthority('BAC_SY')")
    @Transactional
    public ResponseEntity<?> dangKyLichKham(@AuthenticationPrincipal CustomUserDetails taiKhoan,
                                            @RequestBody LichKhamBacSy schedule) {
        if (taiKhoan == null || !taiKhoan.hasRole("BAC_SY")) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Bạn không có quyền truy cập!");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            if (schedule.getNgayKham() == null || schedule.getKhungGio() == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Ngày khám và khung giờ là bắt buộc!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Kiểm tra ngày Chủ Nhật và ngày lễ
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(schedule.getNgayKham());
            boolean isSunday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String selectedDate = dateFormat.format(schedule.getNgayKham());

            // Danh sách ngày lễ cố định
            List<String> holidays = Arrays.asList(
                    "2025-01-29", // Tết Nguyên Đán (Mùng 1 Tết)
                    "2025-01-30", // Mùng 2 Tết
                    "2025-01-31", // Mùng 3 Tết
                    "2025-04-30", // Ngày Thống nhất
                    "2025-05-01", // Ngày Quốc tế Lao động
                    "2025-09-02"  // Quốc khánh
            );

            if (isSunday || holidays.contains(selectedDate)) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Không thể đăng ký lịch khám vào Chủ Nhật hoặc ngày lễ!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            TaiKhoan taiKhoanEntity = taiKhoan.getTaiKhoan();
            BacSy bacSy = bacSyRepo.findByTaiKhoan(taiKhoanEntity);
            if (bacSy == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Không tìm thấy thông tin bác sĩ!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            schedule.setMaBacSy(bacSy.getId());

            // Kiểm tra lịch trùng
            calendar.setTime(schedule.getNgayKham());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date startDate = calendar.getTime();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            Date endDate = calendar.getTime();

            List<LichKhamBacSy> existingSchedules = lichKhamBacSyRepository.findByMaBacSyAndNgayKhamBetween(
                    bacSy.getId(), startDate, endDate);
            if (existingSchedules.stream().anyMatch(s -> s.getKhungGio().equals(schedule.getKhungGio()))) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Đã có lịch khám trong khung giờ này!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            schedule.setThoiGianTao(new Date());
            schedule.setThoiGianCapNhat(new Date());
            schedule.setNgayTrongTuan(getDayOfWeek(schedule.getNgayKham()));
            LichKhamBacSy savedSchedule = lichKhamBacSyRepository.save(schedule);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Đăng ký lịch khám thành công!");
            response.put("id", savedSchedule.getId().toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Đăng ký lịch khám thất bại: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Đăng ký lịch khám thất bại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private String getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String[] days = {"Chủ Nhật", "Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy"};
        return days[dayOfWeek - 1];
    }

    @GetMapping("/bacsy/danhSachLichKhamCuaToi")
    @PreAuthorize("hasAuthority('BAC_SY')")
    public ResponseEntity<?> getDanhSachLichKhamCuaToi(@AuthenticationPrincipal CustomUserDetails taiKhoan) {
        if (taiKhoan == null || !taiKhoan.hasRole("BAC_SY")) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Bạn không có quyền truy cập!");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            TaiKhoan taiKhoanEntity = taiKhoan.getTaiKhoan();
            BacSy bacSy = bacSyRepo.findByTaiKhoan(taiKhoanEntity);
            if (bacSy == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Không tìm thấy thông tin bác sĩ!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            List<LichKhamBacSy> schedules = lichKhamBacSyRepository.findByMaBacSy(bacSy.getId());
            if (schedules.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Không có lịch khám nào!");
                response.put("schedules", schedules);
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            logger.error("Lấy danh sách lịch khám thất bại: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Lấy danh sách lịch khám thất bại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/bacsy/capNhatLichKham/{id}")
    @PreAuthorize("hasAuthority('BAC_SY')")
    @Transactional
    public ResponseEntity<?> capNhatLichKham(@PathVariable Long id, @RequestBody LichKhamBacSy schedule,
                                             @AuthenticationPrincipal CustomUserDetails taiKhoan) {
        if (taiKhoan == null || !taiKhoan.hasRole("BAC_SY")) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Bạn không có quyền truy cập!");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            Optional<LichKhamBacSy> existingSchedule = lichKhamBacSyRepository.findById(id);
            if (existingSchedule.isPresent()) {
                TaiKhoan taiKhoanEntity = taiKhoan.getTaiKhoan();
                BacSy bacSy = bacSyRepo.findByTaiKhoan(taiKhoanEntity);
                if (bacSy == null || !existingSchedule.get().getMaBacSy().equals(bacSy.getId())) {
                    Map<String, String> response = new HashMap<>();
                    response.put("error", "Bạn không có quyền cập nhật lịch này!");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                }
                schedule.setId(id);
                schedule.setMaBacSy(bacSy.getId());
                schedule.setThoiGianCapNhat(new Date());
                schedule.setNgayTrongTuan(getDayOfWeek(schedule.getNgayKham()));
                LichKhamBacSy updatedSchedule = lichKhamBacSyRepository.save(schedule);
                Map<String, String> response = new HashMap<>();
                response.put("message", "Cập nhật lịch khám thành công!");
                return ResponseEntity.ok(response);
            }
            Map<String, String> response = new HashMap<>();
            response.put("error", "Lịch khám không tồn tại!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            logger.error("Cập nhật lịch khám thất bại: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Cập nhật lịch khám thất bại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/bacsy/xoaLichKham/{id}")
    @PreAuthorize("hasAuthority('BAC_SY')")
    @Transactional
    public ResponseEntity<?> xoaLichKham(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails taiKhoan) {
        if (taiKhoan == null || !taiKhoan.hasRole("BAC_SY")) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Bạn không có quyền truy cập!");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            Optional<LichKhamBacSy> existingSchedule = lichKhamBacSyRepository.findById(id);
            if (existingSchedule.isPresent()) {
                TaiKhoan taiKhoanEntity = taiKhoan.getTaiKhoan();
                BacSy bacSy = bacSyRepo.findByTaiKhoan(taiKhoanEntity);
                if (bacSy == null || !existingSchedule.get().getMaBacSy().equals(bacSy.getId())) {
                    Map<String, String> response = new HashMap<>();
                    response.put("error", "Bạn không có quyền xóa lịch này!");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                }
                lichKhamBacSyRepository.deleteById(id);
                Map<String, String> response = new HashMap<>();
                response.put("message", "Xóa lịch khám thành công!");
                return ResponseEntity.ok(response);
            }
            Map<String, String> response = new HashMap<>();
            response.put("error", "Lịch khám không tồn tại!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            logger.error("Xóa lịch khám thất bại: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Xóa lịch khám thất bại: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}