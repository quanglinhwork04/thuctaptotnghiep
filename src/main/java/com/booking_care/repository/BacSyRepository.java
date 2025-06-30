package com.booking_care.repository;

import com.booking_care.constant.ewewwewe.Status;
import com.booking_care.model.BacSy;
import com.booking_care.model.TaiKhoan;
import com.booking_care.model.response.BacSyDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BacSyRepository extends JpaRepository<BacSy, Integer> {

    @Modifying
    @Query("update BacSy b set b.ngaySinh = ?1, b.hoTen = ?2, b.chucVu = ?3, b.chuyenKhoa.id = ?4, b.sdt = ?5, b.email = ?6, b.chungChi = ?7, b.kinhNghiem = ?8, b.linhVucChuyenSau = ?9, b.tienKham = ?10, b.noiKham = ?11 where b.id = ?12")
    void updateThongTinBacSy(Date ngaySinh, String hoTen, String chucVu, Integer chuyenKhoaId, String sdt, String email, String chungChi, String kinhNghiem, String linhVucChuyenSau, Integer tienKham, String noiKham, Integer id);

    @Modifying
    @Query("update BacSy b set b.ngaySinh = ?1, b.hoTen = ?2, b.chucVu = ?3, b.chuyenKhoa.id = ?4, b.sdt = ?5, b.email = ?6, b.photo = ?7, b.chungChi = ?8, b.kinhNghiem = ?9, b.linhVucChuyenSau = ?10, b.tienKham = ?11, b.noiKham = ?12 where b.id = ?13")
    void updateThongTinBacSyAndUploadFile(Date ngaySinh, String hoTen, String chucVu, Integer chuyenKhoaId, String sdt, String email, String photo, String chungChi, String kinhNghiem, String linhVucChuyenSau, Integer tienKham, String noiKham, Integer id);

    @Query("select b from BacSy b where b.chuyenKhoa.id = ?1")
    List<BacSy> getAllByChuyenKhoa(Integer idChuyenKhoa);

    BacSy findByTaiKhoan(TaiKhoan taiKhoan);

    @Query("select b from BacSy b where b.chuyenKhoa.id = ?1 and b.hoTen like %?2%")
    Page<BacSy> getAllByChuyenKhoaAndName(Integer idCk, String bsName, Pageable pageable);

    @Query("select b from BacSy b where b.hoTen like %?1%")
    Page<BacSy> getAllByName(String bsName, Pageable pageable);

    @Query("select b from BacSy b where b.id is not null and (:keyword is null or lower(b.hoTen) like lower(concat('%', :keyword, '%')) " +
            "or lower(b.chuyenKhoa.ten) like lower(concat('%', :keyword, '%')) " +
            "or lower(b.noiKham) like lower(concat('%', :keyword, '%')))")
    Page<BacSy> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select b from BacSy b where b.chuyenKhoa.id = :chuyenKhoaId")
    Page<BacSy> findByChuyenKhoaId(@Param("chuyenKhoaId") Integer chuyenKhoaId, Pageable pageable);

    @Query("select b from BacSy b where b.id is not null and (:keyword is null or lower(b.hoTen) like lower(concat('%', :keyword, '%')) " +
            "or lower(b.noiKham) like lower(concat('%', :keyword, '%')) " +
            "or lower(b.linhVucChuyenSau) like lower(concat('%', :keyword, '%'))) and b.chuyenKhoa.id = :chuyenKhoaId")
    Page<BacSy> findByKeywordAndChuyenKhoa(@Param("keyword") String keyword, @Param("chuyenKhoaId") Integer chuyenKhoaId, Pageable pageable);

    @Query("SELECT b FROM BacSy b WHERE b.chuyenKhoa.id = :chuyenKhoaId AND b.taiKhoan.vaiTro.ten = :vaiTro")
    List<BacSy> findByChuyenKhoaIdAndTaiKhoanVaiTroTen(@Param("chuyenKhoaId") Integer chuyenKhoaId, @Param("vaiTro") String vaiTro);

    // Thêm phương thức mới
    boolean existsByIdAndChuyenKhoaId(Integer id, Integer chuyenKhoaId);

    // Lấy danh sách bác sĩ có lịch trống theo chuyên khoa và khung giờ
    @Query("SELECT b FROM BacSy b WHERE b.chuyenKhoa.id = :chuyenKhoaId AND b.id NOT IN (SELECT l.bacSy.id FROM LichKham l WHERE l.ngayKham = :ngayKham AND l.khungGioKham = :khungGioKham AND l.status NOT IN (:excludedStatuses))")
    List<BacSy> findAvailableDoctorsByChuyenKhoaAndTime(@Param("chuyenKhoaId") Integer chuyenKhoaId, @Param("ngayKham") Date ngayKham, @Param("khungGioKham") Integer khungGioKham, @Param("excludedStatuses") List<Status> excludedStatuses);

    @Query("SELECT b.id, COUNT(l.id) as waitingCount FROM BacSy b LEFT JOIN LuotKham l ON b.id = l.bacSy.id WHERE b.chuyenKhoa.id = :chuyenKhoaId AND l.trangThai = 'Chờ xử lý' AND l.ngayKham = :ngayKham GROUP BY b.id ORDER BY waitingCount ASC")
    List<Object[]> findDoctorsWithWaitingCountByChuyenKhoaAndDate(Integer chuyenKhoaId, java.util.Date ngayKham);
}