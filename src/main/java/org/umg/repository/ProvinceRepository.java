package org.umg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.umg.model.Province;

import java.util.List;

public interface ProvinceRepository extends JpaRepository<Province, Long> {
    List<Province> findByRegionIso(String regionIso);
}