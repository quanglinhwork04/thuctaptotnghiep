package com.booking_care.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "luot_kham")
public class LuotKham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_dat_lich")
    private Integer maDatLich;

    @ManyToOne
    @JoinColumn(name = "id_bac_sy")
    private BacSy bacSy;

    @ManyToOne
    @JoinColumn(name = "id_benh_nhan")
    private BenhNhan benhNhan;

    @Column(name = "ten_benh_nhan")
    private String tenBenhNhan;

    @Column(name = "ngay_sinh_benh_nhan")
    @Temporal(TemporalType.DATE)
    private Date ngaySinhBenhNhan;

    @Column(name = "ly_do_kham")
    private String lyDoKham;

    @Column(name = "sdt_benh_nhan")
    private String sdtBenhNhan;

    @Column(name = "so_thu_tu")
    private Integer soThuTu;

    @Column(name = "vi_tri")
    private Integer viTri;

    @Column(name = "gio_kham")
    @Temporal(TemporalType.TIMESTAMP)
    private Date gioKham;

    @Column(name = "ngay_kham")
    @Temporal(TemporalType.DATE)
    private Date ngayKham;

    @Column(name = "trang_thai")
    private String trangThai;

    @Column(name = "ngay_tao")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayTao;

    @Column(name = "ngay_cap_nhat")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayCapNhat;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getMaDatLich() { return maDatLich; }
    public void setMaDatLich(Integer maDatLich) { this.maDatLich = maDatLich; }
    public BacSy getBacSy() { return bacSy; }
    public void setBacSy(BacSy bacSy) { this.bacSy = bacSy; }
    public BenhNhan getBenhNhan() { return benhNhan; }
    public void setBenhNhan(BenhNhan benhNhan) { this.benhNhan = benhNhan; }
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
    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }
    public Date getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(Date ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }
}