package com.oem.evwarranty.repository;

import com.oem.evwarranty.model.ClaimAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository for ClaimAttachment entity operations.
 */
@Repository
public interface ClaimAttachmentRepository extends JpaRepository<ClaimAttachment, Long> {
    List<ClaimAttachment> findByWarrantyClaimId(Long claimId);
}
