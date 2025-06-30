package com.booking_care.model.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class LuotKhamRequest {
    private Integer id;
    private Integer maDatLich;
    @NotNull(message = "Chuyên khoa không được để trống")
    private Integer chuyenKhoaId;
    private Integer bacSyId;
    @NotNull(message = "Bệnh nhân không được để trống")
    private Integer benhNhanId;
    @NotBlank(message = "Tên bệnh nhân không được để trống")
    private String tenBenhNhan;
    @NotNull(message = "Ngày sinh không được để trống")
    private Date ngaySinhBenhNhan;
    @NotBlank(message = "Lý do khám không được để trống")
    private String lyDoKham;
    @NotBlank(message = "Số điện thoại không được để trống")
    private String sdtBenhNhan;
    private Integer soThuTu;
    private Integer viTri;
    @NotNull(message = "Giờ khám không được để trống")
    private Date gioKham;
    @NotNull(message = "Ngày khám không được để trống")
    private Date ngayKham;
    @NotBlank(message = "Trạng thái không được để trống")
    private String trangThai;
    private Boolean autoAssign = false;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getMaDatLich() { return maDatLich; }
    public void setMaDatLich(Integer maDatLich) { this.maDatLich = maDatLich; }
    public Integer getChuyenKhoaId() { return chuyenKhoaId; }
    public void setChuyenKhoaId(Integer chuyenKhoaId) { this.chuyenKhoaId = chuyenKhoaId; }
    public Integer getBacSyId() { return bacSyId; }
    public void setBacSyId(Integer bacSyId) { this.bacSyId = bacSyId; }
    public Integer getBenhNhanId() { return benhNhanId; }
    public void setBenhNhanId(Integer benhNhanId) { this.benhNhanId = benhNhanId; }
    public String getTenBenhNhan() { return tenBenhNhan; }
    public void setTenBenhNhan(String tenBenhNhan) { this.tenBenhNhan = tenBenhNhan; }
    public Date getNgaySinhBenhNhan() { return ngaySinhBenhNhan; }
    public void setNgaySinhBenhNhan(Date ngaySinhBenhNhan) { this.ngaySinhBenhNhan = ngaySinhBenhNhan; }
    public String getLyDoKham() { return lyDoKham; }
    public void setLyDoKham(String lyDoKham) { this.lyDoKham = lyDoKham; }
    public String getSdtBenhNhan() { return sdtBenhNhan; }
    public void setSdtBenhNhan(String sdtBenhNhan) { this.sdtBenhNhan = sdtBenhNhan; }
    public Integer getSoThuTu() { return soThuTu; }
    public void setSoThuTu(Integer soThuTu) { this.soThuTu = soThuTu; }
    public Integer getViTri() { return viTri; }
    public void setViTri(Integer viTri) { this.viTri = viTri; }
    public Date getGioKham() { return gioKham; }
    public void setGioKham(Date gioKham) { this.gioKham = gioKham; }
    public Date getNgayKham() { return ngayKham; }
    public void setNgayKham(Date ngayKham) { this.ngayKham = ngayKham; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public Boolean getAutoAssign() { return autoAssign; }
    public void setAutoAssign(Boolean autoAssign) { this.autoAssign = autoAssign; }
}