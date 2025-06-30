package com.booking_care.repository;

import com.booking_care.constant.ewewwewe.Status;
import com.booking_care.model.BacSy;
import com.booking_care.model.LichKham;
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
public interface LichKhamRepository extends JpaRepository<LichKham, Integer> {

    // Lấy lịch khám của bệnh nhân theo ID, sắp xếp theo ngày khám giảm dần
    @Query("SELECT l FROM LichKham l WHERE l.benhNhan.id = :benhNhanId ORDER BY l.ngayKham DESC")
    List<LichKham> getAllLichKhamOfBenhNhan(@Param("benhNhanId") Integer benhNhanId, Pageable pageable);

    // Lấy lịch khám của bệnh nhân theo ID và trạng thái
    @Query("SELECT l FROM LichKham l WHERE l.benhNhan.id = :benhNhanId AND l.status = :status ORDER BY l.ngayKham DESC")
    List<LichKham> getAllLichKhamOfBenhNhanByStatus(@Param("benhNhanId") Integer benhNhanId, @Param("status") Status status, Pageable pageable);

    // Lấy lịch khám theo bác sĩ
    @Query("SELECT l FROM LichKham l WHERE l.bacSy.id = :bacSyId")
    List<LichKham> getLichKhamByBacSyId(@Param("bacSyId") Integer bacSyId, Pageable pageable);

    // Lấy lịch khám theo bác sĩ và trạng thái
    @Query("SELECT l FROM LichKham l WHERE l.bacSy.id = :bacSyId AND l.status = :status")
    List<LichKham> getLichKhamByBacSyIdAndStatus(@Param("bacSyId") Integer bacSyId, @Param("status") Status status, Pageable pageable);

    // Tìm lịch khám theo ngày, bác sĩ và khung giờ
    @Query("SELECT l FROM LichKham l WHERE l.ngayKham = :ngayKham AND l.bacSy.id = :bacSyId AND l.khungGioKham = :khungGioKham")
    List<LichKham> findByNgayKhamAndBacSyIdAndKhungGioKham(
            @Param("ngayKham") Date ngayKham, @Param("bacSyId") Integer bacSyId, @Param("khungGioKham") Integer khungGioKham);

    // Tìm lịch khám theo ngày, bác sĩ, khung giờ và trạng thái
    @Query("SELECT l FROM LichKham l WHERE l.ngayKham = :ngayKham AND l.bacSy.id = :bacSyId AND l.khungGioKham = :khungGioKham AND l.status IN :statuses")
    List<LichKham> findByNgayKhamAndBacSyIdAndKhungGioKhamAndStatusIn(
            @Param("ngayKham") Date ngayKham, @Param("bacSyId") Integer bacSyId, @Param("khungGioKham") Integer khungGioKham, @Param("statuses") List<Status> statuses);

    // Kiểm tra lịch khám của bệnh nhân với trạng thái CHO_XU_LY hoặc DA_XAC_NHAN
    @Query(value = "SELECT * FROM bookingcare.lich_kham WHERE id_benh_nhan = :benhNhanId AND status IN (0, 1)", nativeQuery = true)
    List<LichKham> existsByBenhNhan(@Param("benhNhanId") Integer benhNhanId);

    // Lấy lịch khám trong tuần của bác sĩ
    @Query(value = "SELECT * FROM bookingcare.lich_kham WHERE id_bac_sy = :bacSyId AND status = 1 AND DATE(ngay_kham) BETWEEN DATE(SUBDATE(NOW(), WEEKDAY(NOW()))) AND DATE(SUBDATE(NOW(), WEEKDAY(NOW()) - 7)) ORDER BY ngay_kham", nativeQuery = true)
    List<LichKham> getLichKhamTrongTuan(@Param("bacSyId") Integer bacSyId);

    // Lấy tất cả lịch khám của bác sĩ
    @Query("SELECT l FROM LichKham l WHERE l.bacSy.id = :bacSyId ORDER BY l.ngayKham DESC")
    List<LichKham> getAllLichKhamByBacSyId(@Param("bacSyId") Integer bacSyId);

    // Đếm số lịch khám theo bác sĩ và ngày
    @Query(value = "SELECT COUNT(*) FROM lich_kham WHERE id_bac_sy = :bacSyId AND DATE(ngay_kham) = DATE(:today) AND status IN (0, 1, 2, 3)", nativeQuery = true)
    long countAppointmentsByDoctorAndDate(@Param("bacSyId") Integer bacSyId, @Param("today") Date date);

    // Đếm số bệnh nhân khác nhau của bác sĩ
    @Query("SELECT COUNT(DISTINCT l.benhNhan.id) FROM LichKham l WHERE l.bacSy.id = :bacSyId")
    long countDistinctPatientsByDoctor(@Param("bacSyId") Integer bacSyId);

    // Tính doanh thu theo bác sĩ và tháng
    @Query("SELECT COALESCE(SUM(COALESCE(l.tienKham, 0)), 0) FROM LichKham l WHERE l.bacSy.id = :bacSyId AND YEAR(l.ngayKham) = :year AND MONTH(l.ngayKham) = :month AND l.status = 3")
    double sumRevenueByDoctorAndMonth(@Param("bacSyId") Integer bacSyId, @Param("year") int year, @Param("month") int month);

    // Đếm số lịch khám theo bác sĩ và ngày cụ thể
    @Query(value = "SELECT COUNT(*) FROM lich_kham WHERE id_bac_sy = :bacSyId AND ngay_kham LIKE CONCAT(:date, '%') AND status IN (1, 3)", nativeQuery = true)
    long countAppointmentsByDoctorAndSpecificDate(@Param("bacSyId") Integer bacSyId, @Param("date") String date);

    // Lấy lịch khám theo ngày và bác sĩ, sắp xếp theo thứ tự
    @Query("SELECT l FROM LichKham l WHERE l.ngayKham = :ngayKham AND l.bacSy.id = :bacSyId ORDER BY l.orderNumber ASC")
    Page<LichKham> findByNgayKhamAndBacSyIdOrderByOrderNumber(@Param("ngayKham") Date ngayKham, @Param("bacSyId") Integer bacSyId, Pageable pageable);

    // Lấy lịch khám theo ngày, bác sĩ và chuyên khoa, sắp xếp theo thứ tự
    @Query("SELECT l FROM LichKham l WHERE l.ngayKham = :ngayKham AND l.bacSy.id = :bacSyId AND l.chuyenKhoaId = :chuyenKhoaId ORDER BY l.orderNumber ASC")
    Page<LichKham> findByNgayKhamAndBacSyIdAndChuyenKhoaIdOrderByOrderNumber(@Param("ngayKham") Date ngayKham, @Param("bacSyId") Integer bacSyId, @Param("chuyenKhoaId") Integer chuyenKhoaId, Pageable pageable);

    // Lấy lịch khám theo ngày
    @Query("SELECT l FROM LichKham l WHERE l.ngayKham = :ngayKham")
    Page<LichKham> findByNgayKham(@Param("ngayKham") Date ngayKham, Pageable pageable);

    // Lấy lịch khám theo ngày, bác sĩ, chuyên khoa và trạng thái
    @Query("SELECT l FROM LichKham l WHERE l.ngayKham = :ngayKham AND l.bacSy.id = :bacSyId AND l.chuyenKhoaId = :chuyenKhoaId AND l.status = :status")
    Page<LichKham> findByNgayKhamAndBacSyIdAndChuyenKhoaIdAndStatus(@Param("ngayKham") Date ngayKham, @Param("bacSyId") Integer bacSyId, @Param("chuyenKhoaId") Integer chuyenKhoaId, @Param("status") Status status, Pageable pageable);

    // Tìm kiếm lịch khám theo từ khóa
    @Query("SELECT l FROM LichKham l WHERE l.benhNhan.hoTen LIKE %:search% OR l.moTaTrieuChung LIKE %:search%")
    Page<LichKham> findBySearchTerm(@Param("search") String search, Pageable pageable);

    // Cập nhật số thứ tự
    @Modifying
    @Query("UPDATE LichKham l SET l.orderNumber = :orderNumber WHERE l.id = :id")
    void updateOrderNumber(@Param("id") Integer id, @Param("orderNumber") Integer orderNumber);

    // Tìm bác sĩ ít bận nhất trong ngày theo chuyên khoa
    @Query("SELECT l.bacSy, COUNT(l) as appointmentCount FROM LichKham l WHERE l.ngayKham = :ngayKham AND l.chuyenKhoaId = :chuyenKhoaId AND l.status IN (0, 1, 2, 3) GROUP BY l.bacSy ORDER BY appointmentCount ASC")
    List<Object[]> findLeastBusyDoctorByChuyenKhoa(@Param("ngayKham") Date ngayKham, @Param("chuyenKhoaId") Integer chuyenKhoaId, Pageable pageable);

    // Lấy số thứ tự lớn nhất trong ngày cho bác sĩ cụ thể
    @Query("SELECT COALESCE(MAX(l.orderNumber), 0) FROM LichKham l WHERE l.ngayKham = :ngayKham AND l.bacSy.id = :bacSyId")
    Integer findMaxOrderNumberByDateAndDoctor(@Param("ngayKham") Date ngayKham, @Param("bacSyId") Integer bacSyId);

    // Đếm số lịch khám xung đột
    @Query("SELECT COUNT(l) FROM LichKham l WHERE l.ngayKham = :ngayKham AND l.bacSy.id = :bacSyId AND l.khungGioKham = :khungGioKham AND l.status NOT IN :excludedStatuses")
    long countConflictingAppointments(@Param("ngayKham") Date ngayKham, @Param("bacSyId") Integer bacSyId, @Param("khungGioKham") Integer khungGioKham, @Param("excludedStatuses") List<Status> excludedStatuses);

    // Đếm số lịch khám theo ngày, bác sĩ và trạng thái không nằm trong danh sách
    @Query("SELECT COUNT(l) FROM LichKham l WHERE l.ngayKham = :ngayKham AND l.bacSy.id = :bacSyId AND l.status NOT IN :excludedStatuses")
    long countByNgayKhamAndBacSyIdAndStatusNotIn(@Param("ngayKham") Date ngayKham, @Param("bacSyId") Integer bacSyId, @Param("excludedStatuses") List<Status> excludedStatuses);

    // **Phương thức mới**: Lấy số thứ tự tiếp theo cho bác sĩ trong ngày
    @Query("SELECT COALESCE(MAX(l.orderNumber), 0) + 1 FROM LichKham l WHERE l.ngayKham = :ngayKham AND l.bacSy.id = :bacSyId AND l.status NOT IN :excludedStatuses")
    Integer getNextOrderNumber(@Param("ngayKham") Date ngayKham, @Param("bacSyId") Integer bacSyId, @Param("excludedStatuses") List<Status> excludedStatuses);
}