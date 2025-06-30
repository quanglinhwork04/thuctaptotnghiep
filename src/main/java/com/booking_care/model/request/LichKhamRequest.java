package com.booking_care.model.request;

import com.booking_care.constant.ewewwewe.Status;
import javax.validation.constraints.NotNull;

public class LichKhamRequest {
    private Integer id;
    @NotNull
    private Integer benhNhanId;
    @NotNull
    private Integer chuyenKhoaId;
    private Integer bacSyId;
    @NotNull
    private String ngayKham; // Thay thành String
    @NotNull
    private Integer khungGioKham;
    @NotNull
    private Integer tienKham;
    private String moTaTrieuChung;
    private String doctorPreference;
    private Status status; // Thêm trường status
    private boolean isPaid; // Thêm trường isPaid
    private String chanDoan; // Thêm trường chanDoan

    // Getters và Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getBenhNhanId() { return benhNhanId; }
    public void setBenhNhanId(Integer benhNhanId) { this.benhNhanId = benhNhanId; }
    public Integer getChuyenKhoaId() { return chuyenKhoaId; }
    public void setChuyenKhoaId(Integer chuyenKhoaId) { this.chuyenKhoaId = chuyenKhoaId; }
    public Integer getBacSyId() { return bacSyId; }
    public void setBacSyId(Integer bacSyId) { this.bacSyId = bacSyId; }
    public String getNgayKham() { return ngayKham; }
    public void setNgayKham(String ngayKham) { this.ngayKham = ngayKham; }
    public Integer getKhungGioKham() { return khungGioKham; }
    public void setKhungGioKham(Integer khungGioKham) { this.khungGioKham = khungGioKham; }
    public Integer getTienKham() { return tienKham; }
    public void setTienKham(Integer tienKham) { this.tienKham = tienKham; }
    public String getMoTaTrieuChung() { return moTaTrieuChung; }
    public void setMoTaTrieuChung(String moTaTrieuChung) { this.moTaTrieuChung = moTaTrieuChung; }
    public String getDoctorPreference() { return doctorPreference; }
    public void setDoctorPreference(String doctorPreference) { this.doctorPreference = doctorPreference; }

    // Thêm getter và setter cho status
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    // Thêm getter và setter cho isPaid
    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean isPaid) { this.isPaid = isPaid; }

    // Thêm getter và setter cho chanDoan
    public String getChanDoan() { return chanDoan; }
    public void setChanDoan(String chanDoan) { this.chanDoan = chanDoan; }
}