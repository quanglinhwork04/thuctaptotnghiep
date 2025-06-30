package com.booking_care.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "lich_kham_bac_sy")
public class LichKhamBacSy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_bac_sy")
    private Integer maBacSy;

    @Column(name = "ngay_kham")
    @Temporal(TemporalType.DATE)
    private Date ngayKham;

    @Column(name = "khung_gio")
    private String khungGio;

    @Column(name = "ngay_trong_tuan")
    private String ngayTrongTuan;

    @Column(name = "da_san_sang")
    private Boolean daSanSang;

    @Column(name = "thoi_gian_tao")
    @Temporal(TemporalType.TIMESTAMP)
    private Date thoiGianTao;

    @Column(name = "thoi_gian_cap_nhat")
    @Temporal(TemporalType.TIMESTAMP)
    private Date thoiGianCapNhat;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getMaBacSy() { return maBacSy; }
    public void setMaBacSy(Integer maBacSy) { this.maBacSy = maBacSy; }
    public Date getNgayKham() { return ngayKham; }
    public void setNgayKham(Date ngayKham) { this.ngayKham = ngayKham; }
    public String getKhungGio() { return khungGio; }
    public void setKhungGio(String khungGio) { this.khungGio = khungGio; }
    public String getNgayTrongTuan() { return ngayTrongTuan; }
    public void setNgayTrongTuan(String ngayTrongTuan) { this.ngayTrongTuan = ngayTrongTuan; }
    public Boolean getDaSanSang() { return daSanSang; }
    public void setDaSanSang(Boolean daSanSang) { this.daSanSang = daSanSang; }
    public Date getThoiGianTao() { return thoiGianTao; }
    public void setThoiGianTao(Date thoiGianTao) { this.thoiGianTao = thoiGianTao; }
    public Date getThoiGianCapNhat() { return thoiGianCapNhat; }
    public void setThoiGianCapNhat(Date thoiGianCapNhat) { this.thoiGianCapNhat = thoiGianCapNhat; }
}