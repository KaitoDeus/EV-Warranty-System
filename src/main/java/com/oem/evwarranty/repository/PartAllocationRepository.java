package com.oem.evwarranty.repository;

import com.oem.evwarranty.model.PartAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PartAllocationRepository extends JpaRepository<PartAllocation, Long> {
    List<PartAllocation> findByServiceCenter(String sc);

    List<PartAllocation> findByStatus(PartAllocation.AllocationStatus status);
}
