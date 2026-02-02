package com.oem.evwarranty.service;

import com.oem.evwarranty.service.exception.BusinessLogicException;
import com.oem.evwarranty.service.exception.ResourceNotFoundException;
import com.oem.evwarranty.model.User;
import com.oem.evwarranty.model.WarrantyClaim;
import com.oem.evwarranty.repository.UserRepository;
import com.oem.evwarranty.repository.WarrantyClaimRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WarrantyClaimServiceTest {

    @Mock
    private WarrantyClaimRepository claimRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private WarrantyClaimService claimService;

    private WarrantyClaim testClaim;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).username("testadmin").build();
        testClaim = WarrantyClaim.builder()
                .id(1L)
                .claimNumber("WC123")
                .status(WarrantyClaim.ClaimStatus.DRAFT)
                .build();
    }

    @Test
    void testFindById_Success() {
        when(claimRepository.findById(1L)).thenReturn(Optional.of(testClaim));

        Optional<WarrantyClaim> found = claimService.findById(1L);

        assertTrue(found.isPresent());
        assertEquals("WC123", found.get().getClaimNumber());
    }

    @Test
    void testSubmitClaim_Success() {
        when(claimRepository.findById(1L)).thenReturn(Optional.of(testClaim));
        when(claimRepository.save(any(WarrantyClaim.class))).thenReturn(testClaim);

        WarrantyClaim submitted = claimService.submitClaim(1L);

        assertEquals(WarrantyClaim.ClaimStatus.SUBMITTED, submitted.getStatus());
        assertNotNull(submitted.getSubmittedAt());
        verify(auditLogService).log(any(), eq("SUBMIT"), eq("WARRANTY_CLAIM"), anyLong(), anyString());
    }

    @Test
    void testSubmitClaim_Failure_InvalidStatus() {
        testClaim.setStatus(WarrantyClaim.ClaimStatus.APPROVED);
        when(claimRepository.findById(1L)).thenReturn(Optional.of(testClaim));

        assertThrows(BusinessLogicException.class, () -> claimService.submitClaim(1L));
    }

    @Test
    void testApproveClaim_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(claimRepository.findById(1L)).thenReturn(Optional.of(testClaim));
        when(claimRepository.save(any(WarrantyClaim.class))).thenReturn(testClaim);

        WarrantyClaim approved = claimService.approveClaim(1L, 1L);

        assertEquals(WarrantyClaim.ClaimStatus.APPROVED, approved.getStatus());
        assertEquals(testUser, approved.getReviewedBy());
    }

    @Test
    void testApproveClaim_Failure_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> claimService.approveClaim(1L, 1L));
    }
}
