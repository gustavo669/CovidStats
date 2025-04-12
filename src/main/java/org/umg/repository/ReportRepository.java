package org.umg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.umg.model.Report;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByProvinceEntityNameAndDate(String provinceName, String date);
}
