package com.booking_care.controller;

import com.booking_care.constant.ewewwewe.Status;
import com.booking_care.model.*;
import com.booking_care.model.request.BacSyRequest;
import com.booking_care.model.request.DoiMatKhauRequest;
import com.booking_care.model.request.ToaThuocRequest;
import com.booking_care.repository.*;
import com.booking_care.security.CustomUserDetails;
import com.booking_care.service.EmailSenderService;
import com.booking_care.utils.FileUploadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.SendFailedException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class BacSyController {
    private static final Logger logger = LoggerFactory.getLogger(BacSyController.class);

    @Autowired
    public JavaMailSender mailSender;
    @Autowired
    BacSyRepository bacSyRepo;
    @Autowired
    LichKhamRepository lichKhamRepo;
    @Autowired
    ChuyenKhoaRepository chuyenKhoaRepo;
    @Autowired
    TaiKhoanRepository taiKhoanRepo;
    @Autowired
    private ThuocRepository thuocRepo;
    @Autowired
    private ToaThuocRepository toaThuocRepo;
    @Autowired
    private ChiTietToaThuocRepository chiTietToaThuocRepo;
    @Autowired
    EmailSenderService emailSenderService;

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @ModelAttribute
    TaiKhoan taiKhoan() {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user instanceof CustomUserDetails && ((CustomUserDetails) user).hasRole("BENH_NHAN")) {
            return ((CustomUserDetails) user).getTaiKhoan();
        }
        return null;
    }

    @GetMapping("/danh-sach-bac-sy")
    public String getDoctorListPage(
            Model model,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "chuyenKhoaId", required = false) String chuyenKhoaId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        // Truyền các tham số ban đầu để giữ URL
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("chuyenKhoaId", chuyenKhoaId != null ? chuyenKhoaId : "");
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize);

        // Trả về trang để client-side gọi API
        return "list-doctor";
    }

    @GetMapping("/bac-sy/{id}")
    public String getBacSy(@PathVariable Integer id, Model model) {
        BacSy bacSy = bacSyRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bác sĩ không tồn tại"));
        // Xử lý tienKham nếu null
        if (bacSy.getTienKham() == null) {
            logger.warn("tienKham của bác sĩ ID {} là null, gán giá trị mặc định 0", id);
            bacSy.setTienKham(0);
        }
        logger.info("tienKham của bác sĩ ID {}: {}", id, bacSy.getTienKham());

        // Xử lý đường dẫn ảnh
        if (bacSy.getPhoto() != null && !bacSy.getPhoto().isEmpty()) {
            bacSy.setPhoto("/bacsy-photos/" + bacSy.getId() + "/" + bacSy.getPhoto()); // Thêm tiền tố đường dẫn
        } else {
            bacSy.setPhoto("/images/doctor_" + bacSy.getId() + ".png"); // Fallback nếu không có ảnh
        }

        model.addAttribute("bacSy", bacSy);
        return "detail-doctor";
    }

    @GetMapping("/bacsy/login")
    public String viewBacSyLoginPage() {
        return "bacsy/bacsy_login";
    }

    @GetMapping("/bacsy")
    @PreAuthorize("hasAuthority('BAC_SY')")
    public String homeBacSy2() {
        return "bacsy/bacsy_home";
    }

    @GetMapping("/bacsy/home")
    @PreAuthorize("hasAuthority('BAC_SY')")
    public String homeBacSy(Model model, @AuthenticationPrincipal CustomUserDetails taiKhoan) {
        if (taiKhoan == null || !taiKhoan.hasRole("BAC_SY")) {
            logger.error("Không có quyền truy cập hoặc tài khoản null");
            return "redirect:/bacsy/login";
        }

        // Lấy thông tin bác sĩ
        BacSy bacSy = bacSyRepo.findByTaiKhoan(taiKhoan.getTaiKhoan());
        if (bacSy == null) {
            logger.error("Không tìm thấy bác sĩ cho tài khoản: {}", taiKhoan.getUsername());
            return "redirect:/bacsy/login";
        }
        Integer bacSyId = bacSy.getId();

        // Lấy ngày hiện tại
        LocalDate today = LocalDate.now();
        Date todayDate = Date.from(today.atStartOfDay(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant());
        logger.info("Today date: {}", todayDate);

        // Đếm lịch hẹn hôm nay
        long appointmentsToday = lichKhamRepo.countAppointmentsByDoctorAndDate(bacSyId, todayDate);
        logger.info("Appointments today for bacSyId {}: {}", bacSyId, appointmentsToday);

        // Đếm tổng số bệnh nhân
        long totalPatients = lichKhamRepo.countDistinctPatientsByDoctor(bacSyId);
        logger.info("Total patients for bacSyId {}: {}", bacSyId, totalPatients);

        // Tính doanh thu tháng
        int year = today.getYear();
        int month = today.getMonthValue();
        double monthlyRevenue = lichKhamRepo.sumRevenueByDoctorAndMonth(bacSyId, year, month);
        logger.info("Monthly revenue for bacSyId {}: {}", bacSyId, monthlyRevenue);

        // Dữ liệu cho biểu đồ
        List<Long> weeklyAppointments = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            labels.add(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("vi")));
            String sqlDate = date.format(dateFormatter);
            long count = lichKhamRepo.countAppointmentsByDoctorAndSpecificDate(bacSyId, sqlDate);
            weeklyAppointments.add(count);
            logger.info("Date: {}, Count: {}", sqlDate, count);
        }
        logger.info("Weekly appointments: {}", weeklyAppointments);
        logger.info("Labels: {}", labels);

        // Thêm dữ liệu vào model
        model.addAttribute("appointmentsToday", appointmentsToday);
        model.addAttribute("totalPatients", totalPatients);
        model.addAttribute("monthlyRevenue", monthlyRevenue);
        model.addAttribute("bacSy", bacSy);
        model.addAttribute("weeklyAppointments", weeklyAppointments);
        model.addAttribute("labels", labels);

        return "bacsy/bacsy_home";
    }

    @GetMapping("bacsy/danhSachLichKham")
    @PreAuthorize("hasAuthority('BAC_SY')")
    public String getAllLichKham(@AuthenticationPrincipal CustomUserDetails taiKhoan,
                                 Model model) {
        if (taiKhoan == null || !taiKhoan.hasRole("BAC_SY")) {
            return "redirect:/bacsy/login";
        }
        BacSy bacSy = bacSyRepo.findByTaiKhoan(taiKhoan.getTaiKhoan());
        List<LichKham> lichKhamList = lichKhamRepo.getAllLichKhamByBacSyId(bacSy.getId());
        model.addAttribute("lichKhamList", lichKhamList);
        return "bacsy/bacsy_xem_lich_kham";
    }

    @GetMapping("bacsy/xemChiTietLichKham")
    @PreAuthorize("hasAuthority('BAC_SY')")
    public String xemChiTietLichKham(@AuthenticationPrincipal CustomUserDetails taiKhoan,
                                     @RequestParam("id") Integer id,
                                     Model model) {
        if (taiKhoan == null || !taiKhoan.hasRole("BAC_SY")) {
            return "redirect:/bacsy/login";
        }
        LichKham lichKham = lichKhamRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Lịch khám không tồn tại"));
        model.addAttribute("lichKham", lichKham);
        return "bacsy/bacsy_xem_chi_tiet_lich_kham";
    }


    @GetMapping("/bacsy/profile")
    @PreAuthorize("hasAuthority('BAC_SY')")
    public String viewBacSy(Model model, @AuthenticationPrincipal CustomUserDetails taiKhoan) {
        if (taiKhoan == null || !taiKhoan.hasRole("BAC_SY")) {
            return "redirect:/bacsy/login";
        }
        BacSy bacSy = bacSyRepo.findByTaiKhoan(taiKhoan.getTaiKhoan());
        List<ChuyenKhoa> chuyenKhoaList = chuyenKhoaRepo.findAll();
        model.addAttribute("bacSy", bacSy);
        model.addAttribute("chuyenKhoaList", chuyenKhoaList);
        return "bacsy/bacsy_profile";
    }

    @Transactional
    @PostMapping("/bacsy/profile")
    @PreAuthorize("hasAuthority('BAC_SY')")
    public String updateBacSy(RedirectAttributes redirectAttributes, @AuthenticationPrincipal CustomUserDetails taiKhoan,
                              @ModelAttribute(name = "bacSy") @Valid BacSyRequest bacSyRequest, @RequestParam("image") MultipartFile file,
                              Errors errors) {
        if (errors.hasErrors()) {
            return "bacsy/bacsy_profile";
        }
        try {
            BacSy bacSy = bacSyRepo.findByTaiKhoan(taiKhoan.getTaiKhoan());

            if (file.isEmpty()) {
                bacSyRepo.updateThongTinBacSy(format.parse(bacSyRequest.getNgaySinh()), bacSyRequest.getHoTen(),
                        bacSyRequest.getChucVu(), bacSyRequest.getChuyenKhoaId(),
                        bacSyRequest.getSdt(), bacSyRequest.getEmail(), bacSyRequest.getChungChi(), bacSyRequest.getKinhNghiem(),
                        bacSyRequest.getLinhVucChuyenSau(), bacSyRequest.getTienKham(), bacSyRequest.getNoiKham(), bacSy.getId());
                redirectAttributes.addFlashAttribute("ok", "Update thành công");
                return "redirect:/bacsy/profile";
            }
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            bacSyRepo.updateThongTinBacSyAndUploadFile(format.parse(bacSyRequest.getNgaySinh()), bacSyRequest.getHoTen(),
                    bacSyRequest.getChucVu(), bacSyRequest.getChuyenKhoaId(),
                    bacSyRequest.getSdt(), bacSyRequest.getEmail(), fileName, bacSyRequest.getChungChi(),
                    bacSyRequest.getKinhNghiem(), bacSyRequest.getLinhVucChuyenSau(), bacSyRequest.getTienKham(), bacSyRequest.getNoiKham(), bacSy.getId());
            String uploadDir = "bacsy-photos/" + bacSy.getId();
            FileUploadUtil.saveFile(uploadDir, fileName, file);

            redirectAttributes.addFlashAttribute("ok", "Update thành công");
            return "redirect:/bacsy/profile";
        } catch (Exception e) {
            logger.error("Cập nhật thông tin bác sĩ thất bại: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("loi", "Cập nhật thông tin thất bại: " + e.getMessage());
            return "redirect:/bacsy/profile";
        }
    }

    @GetMapping("bacsy/doiMatKhau")
    @PreAuthorize("hasAuthority('BAC_SY')")
    public String viewDoiMatKhauBacSy(Model model, @AuthenticationPrincipal CustomUserDetails taiKhoan) {
        if (taiKhoan == null || !taiKhoan.hasRole("BAC_SY")) {
            return "redirect:/bacsy/login";
        }
        BacSy bacSy = bacSyRepo.findByTaiKhoan(taiKhoan.getTaiKhoan());
        model.addAttribute("bacSy", bacSy);
        model.addAttribute("doiMatKhauRequest", new DoiMatKhauRequest());
        return "bacsy/bacsy_doi_mat_khau";
    }

    @PostMapping("bacsy/doiMatKhau")
    @PreAuthorize("hasAuthority('BAC_SY')")
    @Transactional
    public String updateMatKhauBacSy(@ModelAttribute @Valid DoiMatKhauRequest doiMatKhauRequest,
                                     BindingResult bindingResult,
                                     Model model, @AuthenticationPrincipal CustomUserDetails taiKhoan,
                                     RedirectAttributes redirectAttributes, Errors errors) {
        if (taiKhoan == null || !taiKhoan.hasRole("BAC_SY")) {
            return "redirect:/bacsy/login";
        }

        if (errors.hasErrors()) {
            return "bacsy/bacsy_doi_mat_khau";
        }

        if (!doiMatKhauRequest.getMatKhauHienTai().equals(taiKhoan.getPassword())) {
            redirectAttributes.addFlashAttribute("loiMatKhau1", "Mật khẩu hiện tại sai mật khẩu cũ");
            return "redirect:/bacsy/doiMatKhau";
        }

        try {
            taiKhoanRepo.updateMatKhau(doiMatKhauRequest.getMatKhauMoi(), taiKhoan.getTaiKhoan().getUsername());
            model.addAttribute("ok", "Update mật khẩu thành công");
            return "bacsy/bacsy_doi_mat_khau";
        } catch (Exception e) {
            logger.error("Cập nhật mật khẩu thất bại: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("loi", "Cập nhật mật khẩu thất bại: " + e.getMessage());
            return "redirect:/bacsy/doiMatKhau";
        }
    }

    @GetMapping("bacsy/xemThongKeLichKham")
    @PreAuthorize("hasAuthority('BAC_SY')")
    public String thongKeLichKham(Model model, @AuthenticationPrincipal CustomUserDetails taiKhoan) {
        if (taiKhoan == null || !taiKhoan.hasRole("BAC_SY")) {
            return "redirect:/bacsy/login";
        }
        BacSy bacSy = bacSyRepo.findByTaiKhoan(taiKhoan.getTaiKhoan());
        List<LichKham> lichKhamList = lichKhamRepo.getLichKhamTrongTuan(bacSy.getId());
        model.addAttribute("lichKhamList", lichKhamList);
        return "bacsy/bacsy_thong_ke_lich_kham";
    }

    @GetMapping("/bacsy/dangKyLichKham")
    @PreAuthorize("hasAuthority('BAC_SY')")
    public String viewDangKyLichKham(@AuthenticationPrincipal CustomUserDetails taiKhoan) {
        if (taiKhoan == null || !taiKhoan.hasRole("BAC_SY")) {
            logger.error("Không có quyền truy cập hoặc tài khoản null");
            return "redirect:/bacsy/login";
        }
        return "bacsy/dangKyLichKham"; // Trả về file dangKyLichKham.html
    }

}