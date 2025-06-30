package com.booking_care.repository;

import com.booking_care.model.LuotKham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface LuotKhamRepository extends JpaRepository<LuotKham, Integer> {
    Page<LuotKham> findByNgayKhamAndBacSyIdOrderBySoThuTu(Date ngayKham, Integer bacSyId, Pageable pageable);

    Page<LuotKham> findByNgayKham(Date ngayKham, Pageable pageable);

    @Query("SELECT l FROM LuotKham l WHERE l.tenBenhNhan LIKE %:search% OR l.sdtBenhNhan LIKE %:search%")
    Page<LuotKham> findBySearchTerm(String search, Pageable pageable);

    @Query("SELECT MAX(l.soThuTu) FROM LuotKham l WHERE l.ngayKham = :ngayKham AND l.bacSy.id = :bacSyId")
    Integer findMaxOrderNumberByDateAndDoctor(Date ngayKham, Integer bacSyId);

    @Query("SELECT COUNT(l) FROM LuotKham l WHERE l.ngayKham = :ngayKham AND l.bacSy.id = :bacSyId AND l.gioKham = :gioKham AND l.trangThai NOT IN :excludedStatuses")
    long countConflictingAppointments(Date ngayKham, Integer bacSyId, Date gioKham, List<String> excludedStatuses);

    @Modifying
    @Query("UPDATE LuotKham l SET l.soThuTu = :soThuTu WHERE l.id = :id")
    void updateOrderNumber(Integer id, Integer soThuTu);

    @Query("SELECT l FROM LuotKham l WHERE l.soThuTu > 2 AND l.trangThai = 'Đang xử lý' AND l.ngayKham = :ngayKham AND l.bacSy.id = :bacSyId ORDER BY l.soThuTu")
    List<LuotKham> findSortableAppointments(Date ngayKham, Integer bacSyId);
}