package com.oem.evwarranty.repository;

import com.oem.evwarranty.model.WarrantyPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository for WarrantyPolicy entity operations.
 */
@Repository
public interface WarrantyPolicyRepository extends JpaRepository<WarrantyPolicy, Long> {

    List<WarrantyPolicy> findByIsActiveTrue();

    List<WarrantyPolicy> findByCoverageType(WarrantyPolicy.CoverageType coverageType);
}
