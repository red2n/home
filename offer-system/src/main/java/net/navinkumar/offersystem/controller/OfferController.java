package net.navinkumar.offersystem.controller;

import net.navinkumar.offersystem.dto.OfferDto;
import net.navinkumar.offersystem.dto.OfferEligibilityRequest;
import net.navinkumar.offersystem.dto.OfferEligibilityResponse;
import net.navinkumar.offersystem.dto.OfferApprovalRequest;
import net.navinkumar.offersystem.entity.Offer;
import net.navinkumar.offersystem.entity.OfferApproval;
import net.navinkumar.offersystem.entity.OfferApplication;
import net.navinkumar.offersystem.service.OfferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/offer-system")
@RequiredArgsConstructor
@Slf4j
@Validated
public class OfferController {
    
    private final OfferService offerService;
    
    @PostMapping("/offers")
    public ResponseEntity<Offer> createOffer(
            @Valid @RequestBody OfferDto offerDto,
            @RequestHeader(value = "X-User-ID", defaultValue = "system") String userId) {
        
        log.info("Request to create offer: {}", offerDto.getCode());
        
        try {
            Offer createdOffer = offerService.createOffer(offerDto, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOffer);
        } catch (IllegalArgumentException e) {
            log.error("Error creating offer: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/offers/{id}")
    public ResponseEntity<Offer> updateOffer(
            @PathVariable Long id,
            @Valid @RequestBody OfferDto offerDto,
            @RequestHeader(value = "X-User-ID", defaultValue = "system") String userId) {
        
        log.info("Request to update offer with id: {}", id);
        
        try {
            Offer updatedOffer = offerService.updateOffer(id, offerDto, userId);
            return ResponseEntity.ok(updatedOffer);
        } catch (IllegalArgumentException e) {
            log.error("Error updating offer: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/offers/property/{propertyId}")
    public ResponseEntity<List<Offer>> getValidOffersForProperty(@PathVariable String propertyId) {
        log.info("Request to get valid offers for property: {}", propertyId);
        
        List<Offer> offers = offerService.getValidOffersForProperty(propertyId);
        return ResponseEntity.ok(offers);
    }
    
    @GetMapping("/offers/property/{propertyId}/department/{department}")
    public ResponseEntity<List<Offer>> getValidOffersForDepartment(
            @PathVariable String propertyId,
            @PathVariable String department) {
        
        log.info("Request to get valid offers for property: {} and department: {}", propertyId, department);
        
        List<Offer> offers = offerService.getValidOffersForDepartment(propertyId, department);
        return ResponseEntity.ok(offers);
    }
    
    @PostMapping("/offers/eligibility")
    public ResponseEntity<OfferEligibilityResponse> checkOfferEligibility(@Valid @RequestBody OfferEligibilityRequest request) {
        log.info("Request to check offer eligibility for property: {}", request.getPropertyId());
        
        try {
            OfferEligibilityResponse response = offerService.checkOfferEligibility(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error checking offer eligibility: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/approvals")
    public ResponseEntity<OfferApproval> requestOfferApproval(
            @Valid @RequestBody OfferApprovalRequest request,
            @RequestHeader(value = "X-User-ID", defaultValue = "system") String userId) {
        
        log.info("Request for offer approval for offer id: {}", request.getOfferId());
        
        try {
            OfferApproval approval = offerService.requestOfferApproval(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(approval);
        } catch (IllegalArgumentException e) {
            log.error("Error requesting offer approval: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/approvals/{approvalId}/approve")
    public ResponseEntity<OfferApproval> approveOffer(
            @PathVariable Long approvalId,
            @RequestHeader(value = "X-User-ID", defaultValue = "system") String userId,
            @RequestParam(required = false) String comments) {
        
        log.info("Request to approve offer approval id: {}", approvalId);
        
        try {
            OfferApproval approval = offerService.approveOffer(approvalId, userId, comments);
            return ResponseEntity.ok(approval);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Error approving offer: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/approvals/{approvalId}/reject")
    public ResponseEntity<OfferApproval> rejectOffer(
            @PathVariable Long approvalId,
            @RequestHeader(value = "X-User-ID", defaultValue = "system") String userId,
            @RequestParam String rejectionReason) {
        
        log.info("Request to reject offer approval id: {}", approvalId);
        
        try {
            OfferApproval approval = offerService.rejectOffer(approvalId, userId, rejectionReason);
            return ResponseEntity.ok(approval);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Error rejecting offer: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/offers/{offerCode}/apply")
    public ResponseEntity<OfferApplication> applyOffer(
            @PathVariable String offerCode,
            @RequestParam String reservationId,
            @RequestParam(required = false) String guestId,
            @RequestParam BigDecimal originalAmount,
            @RequestHeader(value = "X-User-ID", defaultValue = "system") String userId) {
        
        log.info("Request to apply offer {} to reservation {}", offerCode, reservationId);
        
        try {
            OfferApplication application = offerService.applyOffer(offerCode, reservationId, guestId, originalAmount, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(application);
        } catch (IllegalArgumentException e) {
            log.error("Error applying offer: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/approvals/department/{department}/pending")
    public ResponseEntity<List<OfferApproval>> getPendingApprovalsForDepartment(@PathVariable String department) {
        log.info("Request to get pending approvals for department: {}", department);
        
        List<OfferApproval> approvals = offerService.getPendingApprovalsForDepartment(department);
        return ResponseEntity.ok(approvals);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Offer System Service is running");
    }
}