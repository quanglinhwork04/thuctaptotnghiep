package com.booking_care.repository;

import com.booking_care.constant.ewewwewe.Status;
import com.booking_care.model.BacSy;
import com.booking_care.model.BenhNhan;
import com.booking_care.model.LichKham;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface LichKhamRepository extends JpaRepository<LichKham, Integer> {
    @Query("select l from LichKham l where l.benhNhan.id = ?1 order by l.ngayKham DESC")
    List<LichKham> getAllLichKhamOfBenhNhan(Integer benhNhanId, Pageable pageable);

    @Query("select l from LichKham l where l.benhNhan.id = ?1 and l.status = ?2 order by l.ngayKham DESC")
    List<LichKham> getAllLichKhamOfBenhNhanByStatus(Integer benhNhanId, Status status, Pageable pageable);

    @Query("select l from LichKham l where l.bacSy.id = ?1")
    List<LichKham> getLichKhamByBacSyId(Integer bacSyId, Pageable pageable);

    @Query("select l from LichKham l where l.bacSy.id = ?1 and l.status = ?2")
    List<LichKham> getLichKhamByBacSyId2(Integer bacSyId, Status status, Pageable pageable);

    @Query("select l from LichKham l where l.ngayKham = ?1 and l.bacSy.id = ?2 and l.khungGioKham = ?3")
    List<LichKham> findByNgayKhamAndBacSyIdAndKhungGioKham(Date ngayKham, Integer bacSyId, Integer khungGioKham);

    @Query(value = "SELECT * FROM bookingcare.lich_kham where id_benh_nhan = ?1 and status in (0,1)", nativeQuery = true)
    List<LichKham> existsByBenhNhan(Integer benhNhanId);

    @Query(value = "select * from bookingcare.lich_kham a WHERE a.id_bac_sy = ?1 and a.status = 1 and date(a.ngay_kham) between date(subdate(now(), weekday(now()))) and date(subdate(now(), weekday(now()) - 7)) order by a.ngay_kham", nativeQuery = true)
    List<LichKham> getLichKhamTrongTuan(Integer bacSyId);

    @Query("select l from LichKham l where l.bacSy.id = :bacSyId order by l.ngayKham DESC")
    List<LichKham> getAllLichKhamByBacSyId(@Param("bacSyId") Integer bacSyId);

    @Query(value = "SELECT COUNT(*) FROM lich_kham WHERE id_bac_sy = :bacSyId AND DATE(ngay_kham) = DATE(:today) AND status IN (0, 1, 2,3)", nativeQuery = true)
    long countAppointmentsByDoctorAndDate(@Param("bacSyId") Integer bacSyId, @Param("today") Date date);

    @Query("SELECT COUNT(DISTINCT l.benhNhan.id) FROM LichKham l WHERE l.bacSy.id = :bacSyId")
    long countDistinctPatientsByDoctor(@Param("bacSyId") Integer bacSyId);

    @Query("SELECT COALESCE(SUM(COALESCE(l.tienKham, 0)), 0) FROM LichKham l WHERE l.bacSy.id = :bacSyId AND YEAR(l.ngayKham) = :year AND MONTH(l.ngayKham) = :month AND l.status = 3 AND l.isPaid = true")
    double sumRevenueByDoctorAndMonth(@Param("bacSyId") Integer bacSyId, @Param("year") int year, @Param("month") int month);

    @Query(value = "SELECT COUNT(*) FROM lich_kham WHERE id_bac_sy = :bacSyId AND ngay_kham LIKE CONCAT(:date, '%') AND status IN (1, 3)", nativeQuery = true)
    long countAppointmentsByDoctorAndSpecificDate(@Param("bacSyId") Integer bacSyId, @Param("date") String date);
}