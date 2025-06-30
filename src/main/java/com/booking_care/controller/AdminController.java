package com.booking_care.controller;

import com.booking_care.constant.Constants;
import com.booking_care.constant.ewewwewe.Status;
import com.booking_care.mapstruct.SimpleMapper;
import com.booking_care.model.*;
import com.booking_care.model.request.*;
import com.booking_care.model.response.BacSyDto;
import com.booking_care.model.response.BenhNhanDto;
import com.booking_care.repository.*;
import com.booking_care.utils.FileUploadUtil;
import com.booking_care.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    BaiDangRepository baiDangRepo;
    @Autowired
    ChuyenMucRepository chuyenMucRepo;
    @Autowired
    ChuyenKhoaRepository chuyenKhoaRepo;
    @Autowired
    TaiKhoanRepository taiKhoanRepo;
    @Autowired
    BenhNhanRepository benhNhanRepo;
    @Autowired
    VaiTroRepository vaiTroRepo;
    @Autowired
    SimpleMapper simpleMapper;
    @Autowired
    BacSyRepository bacSyRepo;
    @Autowired
    LichKhamRepository lichKhamRepo;
    @Autowired
    private LuotKhamRepository luotKhamRepo;

    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Cacheable("bacSyList")
    public List<BacSy> getBacSyList() {
        return bacSyRepo.findAll();
    }

    @Cacheable("chuyenKhoaList")
    public List<ChuyenKhoa> getChuyenKhoaList() {
        return chuyenKhoaRepo.findAll();
    }

    // Trang đăng nhập admin
    @GetMapping("/login")
    public String viewAdminLoginPage() {
        return "admin/admin_login";
    }

    // Trang chủ admin
    @GetMapping("/home")
    public String viewAdminHomePage() {
        return "admin/admin_home";
    }

    // Quản lý chuyên khoa
    @GetMapping("/chuyenkhoa")
    public String viewChuyenKhoaList(Model model) {
        List<ChuyenKhoa> chuyenKhoaList = chuyenKhoaRepo.findAll();
        model.addAttribute("chuyenKhoaList", chuyenKhoaList);
        return "admin/admin_xem_chuyen_khoa";
    }

    @GetMapping("/chuyenkhoa/{id}")
    public String viewChuyenKhoa(Model model, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Optional<ChuyenKhoa> chuyenKhoaOpt = chuyenKhoaRepo.findById(id);
        if (!chuyenKhoaOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy chuyên khoa với ID: " + id);
            return "redirect:/admin/chuyenkhoa";
        }
        model.addAttribute("chuyenKhoa", chuyenKhoaOpt.get());
        return "admin/admin_sua_chuyen_khoa";
    }

    @PostMapping("/chuyenkhoa/{id}")
    @Transactional
    public String editChuyenKhoa(@ModelAttribute("chuyenKhoa") ChuyenKhoaRequest chuyenKhoaRequest, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Optional<ChuyenKhoa> chuyenKhoaOpt = chuyenKhoaRepo.findById(id);
        if (!chuyenKhoaOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy chuyên khoa với ID: " + id);
            return "redirect:/admin/chuyenkhoa";
        }
        chuyenKhoaRepo.updateChuyenKhoa(chuyenKhoaRequest.getTen(), chuyenKhoaRequest.getMoTa(), id);
        redirectAttributes.addFlashAttribute("ok", "Cập nhật chuyên khoa thành công!");
        return "redirect:/admin/chuyenkhoa";
    }

    @GetMapping("/chuyenkhoa/delete/{id}")
    @Transactional
    public String removeChuyenKhoa(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        if (!chuyenKhoaRepo.existsById(id)) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy chuyên khoa với ID: " + id);
            return "redirect:/admin/chuyenkhoa";
        }
        try {
            chuyenKhoaRepo.deleteById(id);
            redirectAttributes.addFlashAttribute("ok", "Xóa chuyên khoa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xóa chuyên khoa!");
            e.printStackTrace();
        }
        return "redirect:/admin/chuyenkhoa";
    }

    @GetMapping("/chuyenkhoa/add")
    public String addChuyenKhoa(Model model) {
        model.addAttribute("chuyenKhoa", new ChuyenKhoa());
        return "admin/admin_them_chuyen_khoa";
    }

    @PostMapping("/chuyenkhoa/add")
    public String addChuyenKhoa(@ModelAttribute("chuyenKhoa") ChuyenKhoaRequest chuyenKhoaRequest, RedirectAttributes redirectAttributes) {
        try {
            chuyenKhoaRepo.save(chuyenKhoaRequest.toChuyenKhoa());
            redirectAttributes.addFlashAttribute("ok", "Thêm mới chuyên khoa thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi thêm chuyên khoa!");
            e.printStackTrace();
        }
        return "redirect:/admin/chuyenkhoa";
    }

    // Quản lý bệnh nhân
    @GetMapping("/benhnhan")
    public String viewBenhNhanList(Model model) {
        List<BenhNhan> benhNhanList = benhNhanRepo.findAll();
        List<BenhNhanDto> benhNhanDtoList = benhNhanList.stream().map(simpleMapper::toBenhNhanDto).collect(Collectors.toList());
        model.addAttribute("benhNhanList", benhNhanDtoList);
        return "admin/admin_xem_benh_nhan";
    }

    @GetMapping("/benhnhan/{id}")
    public String viewBenhNhan(Model model, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Optional<BenhNhan> benhNhanOpt = benhNhanRepo.findById(id);
        if (!benhNhanOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy bệnh nhân với ID: " + id);
            return "redirect:/admin/benhnhan";
        }
        model.addAttribute("benhNhan", benhNhanOpt.get());
        return "admin/admin_sua_benh_nhan";
    }

    @PostMapping("/benhnhan/{id}")
    @Transactional
    public String editBenhNhan(@PathVariable Integer id, @ModelAttribute("benhNhan") @Valid BenhNhanRequest benhNhanRequest,
                               Errors errors, RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            return "admin/admin_sua_benh_nhan";
        }
        Optional<BenhNhan> benhNhanOpt = benhNhanRepo.findById(id);
        if (!benhNhanOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy bệnh nhân với ID: " + id);
            return "redirect:/admin/benhnhan";
        }
        try {
            BenhNhan benhNhan = benhNhanOpt.get();
            benhNhan.setHoTen(benhNhanRequest.getHoTen());
            benhNhan.setSdt(benhNhanRequest.getSdt());
            benhNhan.setEmail(benhNhanRequest.getEmail());
            benhNhan.setDiaChi(benhNhanRequest.getDiaChi());
            benhNhan.setNgaySinh(format.parse(benhNhanRequest.getNgaySinh()));
            benhNhanRepo.save(benhNhan);
            redirectAttributes.addFlashAttribute("ok", "Cập nhật bệnh nhân thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật bệnh nhân!");
            e.printStackTrace();
        }
        return "redirect:/admin/benhnhan/" + id;
    }

    // Quản lý bác sĩ
    @GetMapping("/bacsy")
    public String viewBacSyList(Model model) {
        List<BacSy> bacSyList = bacSyRepo.findAll();
        List<BacSyDto> bacSyDtoList = bacSyList.stream().map(simpleMapper::toBacSyDto).collect(Collectors.toList());
        model.addAttribute("bacSyList", bacSyDtoList);
        return "admin/admin_xem_bac_sy";
    }

    @GetMapping("/bacsy/add")
    public String viewAddBacSy(Model model) {
        model.addAttribute("chuyenKhoaList", chuyenKhoaRepo.findAll());
        model.addAttribute("bacSy", new BacSyResgisterRequest());
        return "admin/admin_them_bac_sy";
    }

    @PostMapping("/bacsy/add")
    @Transactional
    public String addBacSy(@ModelAttribute("bacSy") @Valid BacSyResgisterRequest bacSyRequest, RedirectAttributes redirectAttributes) {
        try {
            String username = Utils.createUserName(bacSyRequest.getHoTen());
            String defaultPw = "123456";
            long count = taiKhoanRepo.countByUsernameStartingWith(username);
            if (count > 0) {
                username += count;
            }
            TaiKhoan taiKhoan = new TaiKhoan();
            taiKhoan.setUsername(username);
            taiKhoan.setPassword(defaultPw);
            Optional<VaiTro> vaiTroOpt = vaiTroRepo.findById(Constants.VaiTro.ROLE_BAC_SY);
            if (!vaiTroOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy vai trò bác sĩ!");
                return "admin/admin_them_bac_sy";
            }
            taiKhoan.setVaiTro(vaiTroOpt.get());
            taiKhoanRepo.save(taiKhoan);

            Optional<ChuyenKhoa> ckOpt = chuyenKhoaRepo.findById(bacSyRequest.getChuyenKhoaId());
            if (!ckOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy chuyên khoa với ID: " + bacSyRequest.getChuyenKhoaId());
                return "admin/admin_them_bac_sy";
            }
            BacSy bacSy = new BacSy(bacSyRequest);
            bacSy.setChuyenKhoa(ckOpt.get());
            bacSy.setNgaySinh(format.parse(bacSyRequest.getNgaySinh()));
            bacSy.setTaiKhoan(taiKhoan);
            bacSy = bacSyRepo.save(bacSy);
            redirectAttributes.addFlashAttribute("ok", "Thêm mới bác sĩ thành công!");
            return "redirect:/admin/bacsy/" + bacSy.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi thêm bác sĩ!");
            e.printStackTrace();
            return "admin/admin_them_bac_sy";
        }
    }

    @GetMapping("/bacsy/{id}")
    public String viewBacSy(Model model, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Optional<BacSy> bacSyOpt = bacSyRepo.findById(id);
        if (!bacSyOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy bác sĩ với ID: " + id);
            return "redirect:/admin/bacsy";
        }
        model.addAttribute("bacSy", bacSyOpt.get());
        model.addAttribute("chuyenKhoaList", chuyenKhoaRepo.findAll());
        return "admin/admin_sua_bac_sy";
    }

    @PostMapping("/bacsy/{id}")
    @Transactional
    public String updateBacSy(@PathVariable Integer id, @ModelAttribute("bacSy") @Valid BacSyRequest bacSyRequest,
                              @RequestParam("image") MultipartFile file, Errors errors, RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            return "admin/admin_sua_bac_sy";
        }
        Optional<BacSy> bacSyOpt = bacSyRepo.findById(id);
        if (!bacSyOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy bác sĩ với ID: " + id);
            return "redirect:/admin/bacsy";
        }
        try {
            if (file.isEmpty()) {
                bacSyRepo.updateThongTinBacSy(format.parse(bacSyRequest.getNgaySinh()), bacSyRequest.getHoTen(),
                        bacSyRequest.getChucVu(), bacSyRequest.getChuyenKhoaId(), bacSyRequest.getSdt(),
                        bacSyRequest.getEmail(), bacSyRequest.getChungChi(), bacSyRequest.getKinhNghiem(),
                        bacSyRequest.getLinhVucChuyenSau(), bacSyRequest.getTienKham(), bacSyRequest.getNoiKham(), id);
            } else {
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                bacSyRepo.updateThongTinBacSyAndUploadFile(format.parse(bacSyRequest.getNgaySinh()), bacSyRequest.getHoTen(),
                        bacSyRequest.getChucVu(), bacSyRequest.getChuyenKhoaId(), bacSyRequest.getSdt(),
                        bacSyRequest.getEmail(), fileName, bacSyRequest.getChungChi(), bacSyRequest.getKinhNghiem(),
                        bacSyRequest.getLinhVucChuyenSau(), bacSyRequest.getTienKham(), bacSyRequest.getNoiKham(), id);
                String uploadDir = "bacsy-photos/" + id;
                FileUploadUtil.saveFile(uploadDir, fileName, file);
            }
            redirectAttributes.addFlashAttribute("ok", "Cập nhật bác sĩ thành công!");
            return "redirect:/admin/bacsy/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật bác sĩ!");
            e.printStackTrace();
            return "redirect:/admin/bacsy/" + id;
        }
    }

    @GetMapping("/bacsy/delete/{id}")
    @Transactional
    public String deleteBacSy(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        if (!bacSyRepo.existsById(id)) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy bác sĩ với ID: " + id);
            return "redirect:/admin/bacsy";
        }
        try {
            bacSyRepo.deleteById(id);
            redirectAttributes.addFlashAttribute("ok", "Xóa bác sĩ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xóa bác sĩ!");
            e.printStackTrace();
        }
        return "redirect:/admin/bacsy";
    }

    // Quản lý bài đăng
    @GetMapping("/baidang")
    public String viewBaiDangList(Model model) {
        List<BaiDang> baiDangList = baiDangRepo.findAll();
        model.addAttribute("baiDangList", baiDangList);
        return "admin/admin_xem_bai_dang";
    }

    @GetMapping("/baidang/{id}")
    public String viewBaiDang(Model model, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Optional<BaiDang> baiDangOpt = baiDangRepo.findById(id);
        if (!baiDangOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy bài đăng với ID: " + id);
            return "redirect:/admin/baidang";
        }
        model.addAttribute("baiDang", baiDangOpt.get());
        return "admin/admin_sua_bai_dang";
    }

    @PostMapping("/baidang/{id}")
    @Transactional
    public String editBaiDang(@PathVariable Integer id, @ModelAttribute("baiDang") BaiDangRequest baiDangRequest,
                              @RequestParam("image") MultipartFile file, RedirectAttributes redirectAttributes) {
        Optional<BaiDang> baiDangOpt = baiDangRepo.findById(id);
        if (!baiDangOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy bài đăng với ID: " + id);
            return "redirect:/admin/baidang";
        }
        try {
            if (file.isEmpty()) {
                baiDangRepo.updateBaiDangKoUploadFile(baiDangRequest.getTen(), baiDangRequest.getChuyenMucId(),
                        baiDangRequest.getTomTat(), baiDangRequest.getNoiDung(), id);
            } else {
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                baiDangRepo.updateBaiDang(baiDangRequest.getTen(), baiDangRequest.getChuyenMucId(),
                        baiDangRequest.getTomTat(), baiDangRequest.getNoiDung(), fileName, id);
                String uploadDir = "baidang-photos/" + id;
                FileUploadUtil.saveFile(uploadDir, fileName, file);
            }
            redirectAttributes.addFlashAttribute("ok", "Cập nhật bài đăng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật bài đăng!");
            e.printStackTrace();
        }
        return "redirect:/admin/baidang/" + id;
    }

    @GetMapping("/baidang/delete/{id}")
    @Transactional
    public String deleteBaiDang(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        if (!baiDangRepo.existsById(id)) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy bài đăng với ID: " + id);
            return "redirect:/admin/baidang";
        }
        try {
            baiDangRepo.deleteById(id);
            redirectAttributes.addFlashAttribute("ok", "Xóa bài đăng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xóa bài đăng!");
            e.printStackTrace();
        }
        return "redirect:/admin/baidang";
    }

    @GetMapping("/baidang/add")
    public String viewAddBaiDang(Model model) {
        model.addAttribute("baiDang", new AddBaiDangRequest());
        model.addAttribute("chuyenMucList", chuyenMucRepo.findAll());
        return "admin/admin_them_bai_dang";
    }

    @PostMapping("/baidang/add")
    @Transactional
    public String addBaiDang(@ModelAttribute("baiDang") @Valid AddBaiDangRequest baiDangRequest,
                             @RequestParam("image") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            if (!chuyenMucRepo.existsById(baiDangRequest.getChuyenMucId())) {
                redirectAttributes.addFlashAttribute("error", "Chưa chọn chuyên mục hợp lệ!");
                return "admin/admin_them_bai_dang";
            }
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Chưa chọn file tải lên!");
                return "admin/admin_them_bai_dang";
            }
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            BaiDang baiDang = new BaiDang();
            baiDang.setTen(baiDangRequest.getTen());
            baiDang.setNoiDung(baiDangRequest.getNoiDung());
            Optional<ChuyenMuc> chuyenMucOpt = chuyenMucRepo.findById(baiDangRequest.getChuyenMucId());
            if (!chuyenMucOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy chuyên mục với ID: " + baiDangRequest.getChuyenMucId());
                return "admin/admin_them_bai_dang";
            }
            baiDang.setChuyenMuc(chuyenMucOpt.get());
            baiDang.setTomTat(baiDangRequest.getTomTat());
            baiDang.setPhoto(fileName);
            BaiDang savedBaiDang = baiDangRepo.save(baiDang);
            String uploadDir = "baidang-photos/" + savedBaiDang.getId();
            FileUploadUtil.saveFile(uploadDir, fileName, file);
            redirectAttributes.addFlashAttribute("ok", "Thêm mới bài đăng thành công!");
            return "redirect:/admin/baidang";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi thêm bài đăng!");
            e.printStackTrace();
            return "admin/admin_them_bai_dang";
        }
    }

    // Quản lý chuyên mục
    @GetMapping("/chuyenmuc")
    public String viewAllChuyenMuc(Model model) {
        List<ChuyenMuc> chuyenMucList = chuyenMucRepo.findAll();
        model.addAttribute("chuyenMuc", chuyenMucList);
        return "admin/admin_xem_chuyen_muc";
    }

    @GetMapping("/chuyenmuc/{id}")
    public String viewChuyenMuc(Model model, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Optional<ChuyenMuc> chuyenMucOpt = chuyenMucRepo.findById(id);
        if (!chuyenMucOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy chuyên mục với ID: " + id);
            return "redirect:/admin/chuyenmuc";
        }
        model.addAttribute("chuyenMuc", chuyenMucOpt.get());
        return "admin/admin_sua_chuyen_muc";
    }

    @PostMapping("/chuyenmuc/{id}")
    @Transactional
    public String editChuyenMuc(@ModelAttribute("chuyenMuc") ChuyenMuc chuyenMuc, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        if (!chuyenMucRepo.existsById(id)) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy chuyên mục với ID: " + id);
            return "redirect:/admin/chuyenmuc";
        }
        try {
            chuyenMuc.setId(id);
            chuyenMucRepo.save(chuyenMuc);
            redirectAttributes.addFlashAttribute("ok", "Cập nhật chuyên mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật chuyên mục!");
            e.printStackTrace();
        }
        return "redirect:/admin/chuyenmuc";
    }

    @GetMapping("/chuyenmuc/delete/{id}")
    @Transactional
    public String removeChuyenMuc(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        if (!chuyenMucRepo.existsById(id)) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy chuyên mục với ID: " + id);
            return "redirect:/admin/chuyenmuc";
        }
        try {
            chuyenMucRepo.deleteById(id);
            redirectAttributes.addFlashAttribute("ok", "Xóa chuyên mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xóa chuyên mục!");
            e.printStackTrace();
        }
        return "redirect:/admin/chuyenmuc";
    }

    @GetMapping("/chuyenmuc/add")
    public String viewAddChuyenMuc(Model model) {
        model.addAttribute("chuyenMuc", new ChuyenMuc());
        return "admin/admin_them_chuyen_muc";
    }

    @PostMapping("/chuyenmuc/add")
    public String addChuyenMuc(@ModelAttribute("chuyenMuc") ChuyenMuc chuyenMuc, RedirectAttributes redirectAttributes) {
        try {
            chuyenMucRepo.save(chuyenMuc);
            redirectAttributes.addFlashAttribute("ok", "Thêm mới chuyên mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi thêm chuyên mục!");
            e.printStackTrace();
        }
        return "redirect:/admin/chuyenmuc";
    }

    // Quản lý lượt khám: Danh sách lượt khám
    @GetMapping("/lich-kham-bac-sy")
    public String viewLichKhamList(Model model,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "size", defaultValue = "10") int size,
                                   @RequestParam(value = "ngayKham", required = false) String ngayKham,
                                   @RequestParam(value = "bacSyId", required = false) Integer bacSyId,
                                   @RequestParam(value = "chuyenKhoaId", required = false) Integer chuyenKhoaId,
                                   @RequestParam(value = "search", required = false) String search,
                                   @RequestParam(value = "orderDir", required = false) String orderDir,
                                   @RequestParam(value = "orderColumn", required = false) String orderColumn,
                                   @RequestParam(value = "status", required = false) String status) {
        Pageable pageable;
        if (orderColumn != null && !orderColumn.isEmpty() && orderDir != null && !orderDir.isEmpty()) {
            Sort sort = Sort.by(orderDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, orderColumn);
            pageable = PageRequest.of(page, size, sort);
        } else {
            pageable = PageRequest.of(page, size);
        }

        Page<LichKham> lichKhamPage;
        Date date = null;
        try {
            if (ngayKham != null && !ngayKham.isEmpty()) {
                date = format.parse(ngayKham);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Định dạng ngày không hợp lệ!");
            e.printStackTrace();
        }

        try {
            if (search != null && !search.isEmpty()) {
                lichKhamPage = lichKhamRepo.findBySearchTerm(search, pageable);
            } else if (date != null && bacSyId != null && chuyenKhoaId != null && status != null && !status.isEmpty()) {
                lichKhamPage = lichKhamRepo.findByNgayKhamAndBacSyIdAndChuyenKhoaIdAndStatus(date, bacSyId, chuyenKhoaId, Status.valueOf(status.toUpperCase()), pageable);
            } else if (date != null && bacSyId != null) {
                lichKhamPage = lichKhamRepo.findByNgayKhamAndBacSyIdOrderByOrderNumber(date, bacSyId, pageable);
            } else if (date != null) {
                lichKhamPage = lichKhamRepo.findByNgayKham(date, pageable);
            } else {
                lichKhamPage = lichKhamRepo.findAll(pageable);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi tải danh sách lượt khám!");
            e.printStackTrace();
            lichKhamPage = Page.empty(pageable);
        }

        model.addAttribute("lichKhamPage", lichKhamPage);
        model.addAttribute("bacSyList", bacSyRepo.findAll());
        model.addAttribute("chuyenKhoaList", chuyenKhoaRepo.findAll());
        model.addAttribute("ngayKham", ngayKham);
        model.addAttribute("selectedBacSyId", bacSyId);
        model.addAttribute("selectedChuyenKhoaId", chuyenKhoaId);
        model.addAttribute("search", search);
        model.addAttribute("orderDir", orderDir);
        model.addAttribute("orderColumn", orderColumn);
        model.addAttribute("status", status);
        return "admin/admin_danh_sach_lich_kham";
    }

    @GetMapping("/luotkham")
    public String viewLuotKhamList(Model model,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "size", defaultValue = "10") int size,
                                   @RequestParam(value = "ngayKham", required = false) String ngayKham,
                                   @RequestParam(value = "bacSyId", required = false) Integer bacSyId,
                                   @RequestParam(value = "search", required = false) String search,
                                   @RequestParam(value = "orderDir", required = false) String orderDir,
                                   @RequestParam(value = "orderColumn", required = false) String orderColumn,
                                   @RequestParam(value = "trangThai", required = false) String trangThai) {
        Pageable pageable;
        if (orderColumn != null && !orderColumn.isEmpty() && orderDir != null && !orderDir.isEmpty()) {
            Sort sort = Sort.by(orderDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, orderColumn);
            pageable = PageRequest.of(page, size, sort);
        } else {
            pageable = PageRequest.of(page, size);
        }

        Page<LuotKham> luotKhamPage;
        Date date = null;
        try {
            if (ngayKham != null && !ngayKham.isEmpty()) {
                date = format.parse(ngayKham);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Định dạng ngày không hợp lệ!");
            e.printStackTrace();
        }

        try {
            if (search != null && !search.isEmpty()) {
                luotKhamPage = luotKhamRepo.findBySearchTerm(search, pageable);
            } else if (date != null && bacSyId != null && trangThai != null && !trangThai.isEmpty()) {
                luotKhamPage = luotKhamRepo.findByNgayKhamAndBacSyIdOrderBySoThuTu(date, bacSyId, pageable);
            } else if (date != null && bacSyId != null) {
                luotKhamPage = luotKhamRepo.findByNgayKhamAndBacSyIdOrderBySoThuTu(date, bacSyId, pageable);
            } else if (date != null) {
                luotKhamPage = luotKhamRepo.findByNgayKham(date, pageable);
            } else {
                luotKhamPage = luotKhamRepo.findAll(pageable);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi tải danh sách lượt khám!");
            e.printStackTrace();
            luotKhamPage = Page.empty(pageable);
        }

        model.addAttribute("luotKhamPage", luotKhamPage);
        model.addAttribute("bacSyList", bacSyRepo.findAll());
        model.addAttribute("chuyenKhoaList", chuyenKhoaRepo.findAll());
        model.addAttribute("ngayKham", ngayKham);
        model.addAttribute("selectedBacSyId", bacSyId);
        model.addAttribute("search", search);
        model.addAttribute("orderDir", orderDir);
        model.addAttribute("orderColumn", orderColumn);
        model.addAttribute("trangThai", trangThai);
        return "admin/admin_danh_sach_luot_kham";
    }

    @GetMapping("/luotkham/add")
    public String viewAddLuotKham(Model model) {
        model.addAttribute("luotKhamRequest", new LuotKhamRequest());
        model.addAttribute("benhNhanList", benhNhanRepo.findAll());
        model.addAttribute("bacSyList", bacSyRepo.findAll());
        model.addAttribute("chuyenKhoaList", chuyenKhoaRepo.findAll());
        return "admin/admin_them_luot_kham";
    }

    @PostMapping("/luotkham/add")
    @Transactional
    public String addLuotKham(@ModelAttribute("luotKhamRequest") @Valid LuotKhamRequest luotKhamRequest,
                              Errors errors, RedirectAttributes redirectAttributes, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("benhNhanList", benhNhanRepo.findAll());
            model.addAttribute("bacSyList", bacSyRepo.findAll());
            model.addAttribute("chuyenKhoaList", chuyenKhoaRepo.findAll());
            return "admin/admin_them_luot_kham";
        }

        try {
            Optional<BenhNhan> benhNhanOpt = benhNhanRepo.findById(luotKhamRequest.getBenhNhanId());
            if (!benhNhanOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy bệnh nhân với ID: " + luotKhamRequest.getBenhNhanId());
                return "redirect:/admin/luotkham/add";
            }

            // Kiểm tra ngày Chủ Nhật và ngày lễ
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(luotKhamRequest.getNgayKham());
            boolean isSunday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String selectedDate = dateFormat.format(luotKhamRequest.getNgayKham());

            List<String> holidays = Arrays.asList(
                    "2025-01-29", "2025-01-30", "2025-01-31", // Tết Nguyên Đán
                    "2025-04-30", // Ngày Thống nhất
                    "2025-05-01", // Ngày Quốc tế Lao động
                    "2025-09-02"  // Quốc khánh
            );

            if (isSunday || holidays.contains(selectedDate)) {
                redirectAttributes.addFlashAttribute("error", "Không thể đăng ký lượt khám vào Chủ Nhật hoặc ngày lễ!");
                return "redirect:/admin/luotkham/add";
            }

            BacSy selectedBacSy;
            if (luotKhamRequest.getAutoAssign() || luotKhamRequest.getBacSyId() == null || luotKhamRequest.getBacSyId() == 0) {
                // Trường hợp 1: Tự động phân công bác sĩ
                List<Object[]> doctorsWithWaitingCount = bacSyRepo.findDoctorsWithWaitingCountByChuyenKhoaAndDate(
                        luotKhamRequest.getChuyenKhoaId(), luotKhamRequest.getNgayKham()
                );
                if (doctorsWithWaitingCount.isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy bác sĩ phù hợp cho chuyên khoa này!");
                    return "redirect:/admin/luotkham/add";
                }
                Integer selectedDoctorId = (Integer) doctorsWithWaitingCount.get(0)[0]; // Chọn bác sĩ có ít bệnh nhân nhất
                Optional<BacSy> bacSyOpt = bacSyRepo.findById(selectedDoctorId);
                if (!bacSyOpt.isPresent()) {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy bác sĩ với ID: " + selectedDoctorId);
                    return "redirect:/admin/luotkham/add";
                }
                selectedBacSy = bacSyOpt.get();
            } else {
                // Trường hợp 2: Chỉ định bác sĩ
                Optional<BacSy> bacSyOpt = bacSyRepo.findById(luotKhamRequest.getBacSyId());
                if (!bacSyOpt.isPresent()) {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy bác sĩ với ID: " + luotKhamRequest.getBacSyId());
                    return "redirect:/admin/luotkham/add";
                }
                selectedBacSy = bacSyOpt.get();
            }

            LuotKham luotKham = new LuotKham();
            luotKham.setMaDatLich(luotKhamRequest.getMaDatLich());
            luotKham.setBacSy(selectedBacSy);
            luotKham.setBenhNhan(benhNhanOpt.get());
            luotKham.setTenBenhNhan(benhNhanOpt.get().getHoTen());
            luotKham.setNgaySinhBenhNhan(luotKhamRequest.getNgaySinhBenhNhan());
            luotKham.setLyDoKham(luotKhamRequest.getLyDoKham());
            luotKham.setSdtBenhNhan(luotKhamRequest.getSdtBenhNhan());
            luotKham.setSoThuTu(luotKhamRequest.getSoThuTu());
            luotKham.setViTri(luotKhamRequest.getViTri());
            luotKham.setGioKham(luotKhamRequest.getGioKham());
            luotKham.setNgayKham(luotKhamRequest.getNgayKham());
            luotKham.setTrangThai(luotKhamRequest.getTrangThai());
            luotKham.setNgayTao(new Date());
            luotKham.setNgayCapNhat(new Date());

            // Kiểm tra xung đột lịch
            List<String> excludedStatuses = Arrays.asList("Đã khám", "Đã hủy");
            long conflictCount = luotKhamRepo.countConflictingAppointments(
                    luotKham.getNgayKham(), luotKham.getBacSy().getId(), luotKham.getGioKham(), excludedStatuses
            );
            if (conflictCount > 0) {
                redirectAttributes.addFlashAttribute("error", "Bác sĩ đã có lịch khám tại khung giờ này!");
                return "redirect:/admin/luotkham/add";
            }

            Integer maxOrder = luotKhamRepo.findMaxOrderNumberByDateAndDoctor(luotKham.getNgayKham(), luotKham.getBacSy().getId());
            luotKham.setSoThuTu(maxOrder != null ? maxOrder + 1 : 1);

            luotKhamRepo.save(luotKham);
            redirectAttributes.addFlashAttribute("ok", "Thêm mới lượt khám thành công!");
            return "redirect:/admin/luotkham";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi thêm lượt khám!");
            e.printStackTrace();
            return "redirect:/admin/luotkham/add";
        }
    }

    @GetMapping("/luotkham/{id}")
    public String viewLuotKham(Model model, @PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Optional<LuotKham> luotKhamOpt = luotKhamRepo.findById(id);
        if (!luotKhamOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy lượt khám với ID: " + id);
            return "redirect:/admin/luotkham";
        }
        LuotKham luotKham = luotKhamOpt.get();
        LuotKhamRequest luotKhamRequest = new LuotKhamRequest();
        luotKhamRequest.setId(id);
        luotKhamRequest.setMaDatLich(luotKham.getMaDatLich());
        luotKhamRequest.setChuyenKhoaId(luotKham.getBacSy().getChuyenKhoa().getId());
        luotKhamRequest.setBacSyId(luotKham.getBacSy().getId());
        luotKhamRequest.setBenhNhanId(luotKham.getBenhNhan().getId());
        luotKhamRequest.setTenBenhNhan(luotKham.getTenBenhNhan());
        luotKhamRequest.setNgaySinhBenhNhan(luotKham.getNgaySinhBenhNhan());
        luotKhamRequest.setLyDoKham(luotKham.getLyDoKham());
        luotKhamRequest.setSdtBenhNhan(luotKham.getSdtBenhNhan());
        luotKhamRequest.setSoThuTu(luotKham.getSoThuTu());
        luotKhamRequest.setViTri(luotKham.getViTri());
        luotKhamRequest.setGioKham(luotKham.getGioKham());
        luotKhamRequest.setNgayKham(luotKham.getNgayKham());
        luotKhamRequest.setTrangThai(luotKham.getTrangThai());

        model.addAttribute("luotKhamRequest", luotKhamRequest);
        model.addAttribute("benhNhanList", benhNhanRepo.findAll());
        model.addAttribute("bacSyList", bacSyRepo.findAll());
        model.addAttribute("chuyenKhoaList", chuyenKhoaRepo.findAll());
        return "admin/admin_sua_luot_kham";
    }

    @PostMapping("/luotkham/{id}")
    @Transactional
    public String editLuotKham(@PathVariable Integer id, @ModelAttribute("luotKhamRequest") @Valid LuotKhamRequest luotKhamRequest,
                               Errors errors, RedirectAttributes redirectAttributes, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("benhNhanList", benhNhanRepo.findAll());
            model.addAttribute("bacSyList", bacSyRepo.findAll());
            model.addAttribute("chuyenKhoaList", chuyenKhoaRepo.findAll());
            return "admin/admin_sua_luot_kham";
        }

        Optional<LuotKham> luotKhamOpt = luotKhamRepo.findById(id);
        if (!luotKhamOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy lượt khám với ID: " + id);
            return "redirect:/admin/luotkham";
        }

        try {
            Optional<BenhNhan> benhNhanOpt = benhNhanRepo.findById(luotKhamRequest.getBenhNhanId());
            if (!benhNhanOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy bệnh nhân với ID: " + luotKhamRequest.getBenhNhanId());
                return "redirect:/admin/luotkham/" + id;
            }

            Optional<BacSy> bacSyOpt = bacSyRepo.findById(luotKhamRequest.getBacSyId());
            if (!bacSyOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy bác sĩ với ID: " + luotKhamRequest.getBacSyId());
                return "redirect:/admin/luotkham/" + id;
            }

            // Kiểm tra ngày Chủ Nhật và ngày lễ
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(luotKhamRequest.getNgayKham());
            boolean isSunday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String selectedDate = dateFormat.format(luotKhamRequest.getNgayKham());

            List<String> holidays = Arrays.asList(
                    "2025-01-29", "2025-01-30", "2025-01-31", // Tết Nguyên Đán
                    "2025-04-30", // Ngày Thống nhất
                    "2025-05-01", // Ngày Quốc tế Lao động
                    "2025-09-02"  // Quốc khánh
            );

            if (isSunday || holidays.contains(selectedDate)) {
                redirectAttributes.addFlashAttribute("error", "Không thể đăng ký lượt khám vào Chủ Nhật hoặc ngày lễ!");
                return "redirect:/admin/luotkham/" + id;
            }

            LuotKham luotKham = luotKhamOpt.get();
            luotKham.setMaDatLich(luotKhamRequest.getMaDatLich());
            luotKham.setBacSy(bacSyOpt.get());
            luotKham.setBenhNhan(benhNhanOpt.get());
            luotKham.setTenBenhNhan(benhNhanOpt.get().getHoTen());
            luotKham.setNgaySinhBenhNhan(luotKhamRequest.getNgaySinhBenhNhan());
            luotKham.setLyDoKham(luotKhamRequest.getLyDoKham());
            luotKham.setSdtBenhNhan(luotKhamRequest.getSdtBenhNhan());
            luotKham.setSoThuTu(luotKhamRequest.getSoThuTu());
            luotKham.setViTri(luotKhamRequest.getViTri());
            luotKham.setGioKham(luotKhamRequest.getGioKham());
            luotKham.setNgayKham(luotKhamRequest.getNgayKham());
            luotKham.setTrangThai(luotKhamRequest.getTrangThai());
            luotKham.setNgayCapNhat(new Date());

            List<String> excludedStatuses = Arrays.asList("Đã khám", "Đã hủy");
            long conflictCount = luotKhamRepo.countConflictingAppointments(luotKham.getNgayKham(), luotKham.getBacSy().getId(), luotKham.getGioKham(), excludedStatuses);
            if (conflictCount > 0 && !luotKham.getId().equals(id)) {
                redirectAttributes.addFlashAttribute("error", "Bác sĩ đã có lịch khám tại khung giờ này!");
                return "redirect:/admin/luotkham/" + id;
            }

            luotKhamRepo.save(luotKham);
            redirectAttributes.addFlashAttribute("ok", "Cập nhật lượt khám thành công!");
            return "redirect:/admin/luotkham";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật lượt khám!");
            e.printStackTrace();
            return "redirect:/admin/luotkham/" + id;
        }
    }

    @GetMapping("/luotkham/delete/{id}")
    @Transactional
    public String deleteLuotKham(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        if (!luotKhamRepo.existsById(id)) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy lượt khám với ID: " + id);
            return "redirect:/admin/luotkham";
        }
        try {
            luotKhamRepo.deleteById(id);
            redirectAttributes.addFlashAttribute("ok", "Xóa lượt khám thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi xóa lượt khám!");
            e.printStackTrace();
        }
        return "redirect:/admin/luotkham";
    }

    @GetMapping("/luotkham/arrange")
    public String viewArrangeLuotKham(Model model,
                                      @RequestParam(value = "ngayKham", required = false) String ngayKham,
                                      @RequestParam(value = "bacSyId", required = false) Integer bacSyId,
                                      @RequestParam(value = "chuyenKhoaId", required = false) Integer chuyenKhoaId,
                                      @RequestParam(value = "page", defaultValue = "0") int page,
                                      @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("soThuTu").ascending());
        Date date = null;
        try {
            if (ngayKham != null && !ngayKham.isEmpty()) {
                date = format.parse(ngayKham);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Định dạng ngày không hợp lệ: " + ngayKham + ". Vui lòng nhập theo định dạng yyyy-MM-dd.");
            e.printStackTrace();
        }

        List<LuotKham> luotKhamList;
        try {
            if (date != null && bacSyId != null) {
                luotKhamList = luotKhamRepo.findSortableAppointments(date, bacSyId);
            } else if (date != null && chuyenKhoaId != null) {
                List<BacSy> bacSyList = bacSyRepo.getAllByChuyenKhoa(chuyenKhoaId);
                luotKhamList = luotKhamRepo.findByNgayKham(date, pageable).getContent().stream()
                        .filter(l -> bacSyList.contains(l.getBacSy()))
                        .filter(l -> l.getSoThuTu() > 2 && l.getTrangThai().equals("Đang xử lý"))
                        .collect(Collectors.toList());
            } else if (date != null) {
                luotKhamList = luotKhamRepo.findByNgayKham(date, pageable).getContent().stream()
                        .filter(l -> l.getSoThuTu() > 2 && l.getTrangThai().equals("Đang xử lý"))
                        .collect(Collectors.toList());
            } else {
                luotKhamList = luotKhamRepo.findAll(pageable).getContent().stream()
                        .filter(l -> l.getSoThuTu() > 2 && l.getTrangThai().equals("Đang xử lý"))
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách lượt khám: " + e.getMessage());
            e.printStackTrace();
            luotKhamList = Collections.emptyList();
        }

        model.addAttribute("luotKhamList", luotKhamList);
        model.addAttribute("bacSyList", bacSyRepo.findAll());
        model.addAttribute("chuyenKhoaList", chuyenKhoaRepo.findAll());
        model.addAttribute("ngayKham", ngayKham);
        model.addAttribute("selectedBacSyId", bacSyId);
        model.addAttribute("selectedChuyenKhoaId", chuyenKhoaId);
        return "admin/admin_sap_xep_luot_kham";
    }

    @PostMapping("/luotkham/arrange")
    @Transactional
    public String arrangeLuotKham(@RequestParam("ids") List<Integer> ids,
                                  @RequestParam("ngayKham") String ngayKham,
                                  @RequestParam("bacSyId") Integer bacSyId,
                                  RedirectAttributes redirectAttributes) {
        try {
            Optional<BacSy> bacSyOpt = bacSyRepo.findById(bacSyId);
            if (!bacSyOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy bác sĩ với ID: " + bacSyId);
                return "redirect:/admin/luotkham/arrange?ngayKham=" + ngayKham;
            }
            Date date = format.parse(ngayKham);
            List<LuotKham> lockedAppointments = luotKhamRepo.findByNgayKhamAndBacSyIdOrderBySoThuTu(date, bacSyId, PageRequest.of(0, 2))
                    .getContent();
            List<Integer> lockedIds = lockedAppointments.stream().map(LuotKham::getId).collect(Collectors.toList());

            for (int i = 0; i < ids.size(); i++) {
                if (!lockedIds.contains(ids.get(i))) {
                    Optional<LuotKham> luotKhamOpt = luotKhamRepo.findById(ids.get(i));
                    if (luotKhamOpt.isPresent()) {
                        luotKhamRepo.updateOrderNumber(ids.get(i), i + 3); // Bắt đầu từ số thứ tự 3
                    }
                }
            }
            redirectAttributes.addFlashAttribute("ok", "Sắp xếp lượt khám thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi sắp xếp lượt khám!");
            e.printStackTrace();
        }
        String redirectUrl = "/admin/luotkham/arrange?ngayKham=" + ngayKham + "&bacSyId=" + bacSyId;
        return "redirect:" + redirectUrl;
    }
}