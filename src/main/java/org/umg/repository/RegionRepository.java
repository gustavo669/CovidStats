package org.umg.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.umg.model.Region;

public interface RegionRepository extends JpaRepository<Region, Long> {
    Region findByIso(String iso);
}
