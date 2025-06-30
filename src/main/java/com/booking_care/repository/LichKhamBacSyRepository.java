package com.booking_care.repository;

import com.booking_care.model.LichKhamBacSy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface LichKhamBacSyRepository extends JpaRepository<LichKhamBacSy, Long> {
    List<LichKhamBacSy> findByMaBacSy(Integer maBacSy);
    List<LichKhamBacSy> findByMaBacSyAndNgayKhamBetween(Integer maBacSy, Date startDate, Date endDate);
}