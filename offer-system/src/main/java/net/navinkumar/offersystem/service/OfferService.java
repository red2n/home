package net.navinkumar.offersystem.service;

import net.navinkumar.offersystem.dto.OfferDto;
import net.navinkumar.offersystem.dto.OfferEligibilityRequest;
import net.navinkumar.offersystem.dto.OfferEligibilityResponse;
import net.navinkumar.offersystem.dto.OfferApprovalRequest;
import net.navinkumar.offersystem.entity.Offer;
import net.navinkumar.offersystem.entity.OfferApproval;
import net.navinkumar.offersystem.entity.OfferApplication;
import net.navinkumar.offersystem.repository.OfferRepository;
import net.navinkumar.offersystem.repository.OfferApprovalRepository;
import net.navinkumar.offersystem.repository.OfferApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OfferService {
    
    private final OfferRepository offerRepository;
    private final OfferApprovalRepository offerApprovalRepository;
    private final OfferApplicationRepository offerApplicationRepository;
    
    public Offer createOffer(OfferDto offerDto, String userId) {
        log.info("Creating offer with code: {}", offerDto.getCode());
        
        // Check if offer code already exists
        if (offerRepository.findByCode(offerDto.getCode()).isPresent()) {
            throw new IllegalArgumentException("Offer with code " + offerDto.getCode() + " already exists");
        }
        
        Offer offer = convertToEntity(offerDto);
        offer.setCreatedBy(userId);
        offer.setUpdatedBy(userId);
        
        return offerRepository.save(offer);
    }
    
    public Offer updateOffer(Long id, OfferDto offerDto, String userId) {
        log.info("Updating offer with id: {}", id);
        
        Offer existingOffer = offerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Offer not found with id: " + id));
        
        // Check if code is being changed and if new code already exists
        if (!existingOffer.getCode().equals(offerDto.getCode()) &&
            offerRepository.findByCode(offerDto.getCode()).isPresent()) {
            throw new IllegalArgumentException("Offer with code " + offerDto.getCode() + " already exists");
        }
        
        updateEntityFromDto(existingOffer, offerDto);
        existingOffer.setUpdatedBy(userId);
        
        return offerRepository.save(existingOffer);
    }
    
    @Transactional(readOnly = true)
    public List<Offer> getValidOffersForProperty(String propertyId) {
        return offerRepository.findValidOffersForProperty(propertyId, LocalDate.now());
    }
    
    @Transactional(readOnly = true)
    public List<Offer> getValidOffersForDepartment(String propertyId, String department) {
        return offerRepository.findValidOffersForDepartment(propertyId, department, LocalDate.now());
    }
    
    @Transactional(readOnly = true)
    public OfferEligibilityResponse checkOfferEligibility(OfferEligibilityRequest request) {
        log.info("Checking offer eligibility for property: {}, room type: {}", 
                request.getPropertyId(), request.getRoomType());
        
        List<Offer> availableOffers = offerRepository.findValidOffersForProperty(request.getPropertyId(), LocalDate.now());
        
        // Filter offers based on eligibility criteria
        List<OfferEligibilityResponse.EligibleOffer> eligibleOffers = availableOffers.stream()
            .filter(offer -> isOfferEligible(offer, request))
            .map(offer -> convertToEligibleOffer(offer, request))
            .collect(Collectors.toList());
        
        // Find the best offer (highest discount)
        OfferEligibilityResponse.EligibleOffer bestOffer = eligibleOffers.stream()
            .max((o1, o2) -> o1.getDiscountAmount().compareTo(o2.getDiscountAmount()))
            .orElse(null);
        
        BigDecimal bestDiscountAmount = bestOffer != null ? bestOffer.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal bestFinalAmount = bestOffer != null ? bestOffer.getFinalAmount() : request.getBaseAmount();
        String recommendedOfferCode = bestOffer != null ? bestOffer.getOfferCode() : null;
        
        return new OfferEligibilityResponse(
            eligibleOffers,
            request.getBaseAmount(),
            bestDiscountAmount,
            bestFinalAmount,
            recommendedOfferCode
        );
    }
    
    public OfferApproval requestOfferApproval(OfferApprovalRequest request, String userId) {
        log.info("Requesting approval for offer id: {}", request.getOfferId());
        
        Offer offer = offerRepository.findById(request.getOfferId())
            .orElseThrow(() -> new IllegalArgumentException("Offer not found with id: " + request.getOfferId()));
        
        OfferApproval approval = new OfferApproval();
        approval.setOffer(offer);
        approval.setReservationId(request.getReservationId());
        approval.setGuestId(request.getGuestId());
        approval.setRequestedBy(userId);
        approval.setComments(request.getComments());
        approval.setStatus(OfferApproval.ApprovalStatus.PENDING);
        
        return offerApprovalRepository.save(approval);
    }
    
    public OfferApproval approveOffer(Long approvalId, String approverUserId, String comments) {
        log.info("Approving offer approval id: {} by user: {}", approvalId, approverUserId);
        
        OfferApproval approval = offerApprovalRepository.findById(approvalId)
            .orElseThrow(() -> new IllegalArgumentException("Offer approval not found with id: " + approvalId));
        
        if (approval.getStatus() != OfferApproval.ApprovalStatus.PENDING) {
            throw new IllegalStateException("Offer approval is not in pending status");
        }
        
        approval.setStatus(OfferApproval.ApprovalStatus.APPROVED);
        approval.setApprovedBy(approverUserId);
        approval.setApprovedAt(LocalDateTime.now());
        approval.setComments(comments);
        
        return offerApprovalRepository.save(approval);
    }
    
    public OfferApproval rejectOffer(Long approvalId, String approverUserId, String rejectionReason) {
        log.info("Rejecting offer approval id: {} by user: {}", approvalId, approverUserId);
        
        OfferApproval approval = offerApprovalRepository.findById(approvalId)
            .orElseThrow(() -> new IllegalArgumentException("Offer approval not found with id: " + approvalId));
        
        if (approval.getStatus() != OfferApproval.ApprovalStatus.PENDING) {
            throw new IllegalStateException("Offer approval is not in pending status");
        }
        
        approval.setStatus(OfferApproval.ApprovalStatus.REJECTED);
        approval.setApprovedBy(approverUserId);
        approval.setApprovedAt(LocalDateTime.now());
        approval.setRejectionReason(rejectionReason);
        
        return offerApprovalRepository.save(approval);
    }
    
    public OfferApplication applyOffer(String offerCode, String reservationId, String guestId, 
                                     BigDecimal originalAmount, String appliedBy) {
        log.info("Applying offer {} to reservation {}", offerCode, reservationId);
        
        Offer offer = offerRepository.findByCode(offerCode)
            .orElseThrow(() -> new IllegalArgumentException("Offer not found with code: " + offerCode));
        
        // Calculate discount
        BigDecimal discountAmount = calculateDiscountAmount(offer, originalAmount);
        BigDecimal finalAmount = originalAmount.subtract(discountAmount);
        
        // Increment usage count
        offer.setUsageCount(offer.getUsageCount() + 1);
        offerRepository.save(offer);
        
        OfferApplication application = new OfferApplication();
        application.setOffer(offer);
        application.setReservationId(reservationId);
        application.setGuestId(guestId);
        application.setOriginalAmount(originalAmount);
        application.setDiscountAmount(discountAmount);
        application.setFinalAmount(finalAmount);
        application.setAppliedBy(appliedBy);
        
        return offerApplicationRepository.save(application);
    }
    
    @Transactional(readOnly = true)
    public List<OfferApproval> getPendingApprovalsForDepartment(String department) {
        return offerApprovalRepository.findPendingApprovalsByDepartment(department, OfferApproval.ApprovalStatus.PENDING);
    }
    
    private boolean isOfferEligible(Offer offer, OfferEligibilityRequest request) {
        // Check room type eligibility
        if (offer.getApplicableRoomTypes() != null && !offer.getApplicableRoomTypes().isEmpty()) {
            List<String> applicableRoomTypes = Arrays.asList(offer.getApplicableRoomTypes().split(","));
            if (!applicableRoomTypes.contains(request.getRoomType())) {
                return false;
            }
        }
        
        // Check rate plan eligibility
        if (offer.getApplicableRatePlans() != null && !offer.getApplicableRatePlans().isEmpty()) {
            List<String> applicableRatePlans = Arrays.asList(offer.getApplicableRatePlans().split(","));
            if (!applicableRatePlans.contains(request.getRatePlanCode())) {
                return false;
            }
        }
        
        // Check stay duration
        if (offer.getMinimumStay() != null && request.getStayDuration() != null && 
            request.getStayDuration() < offer.getMinimumStay()) {
            return false;
        }
        
        if (offer.getMaximumStay() != null && request.getStayDuration() != null && 
            request.getStayDuration() > offer.getMaximumStay()) {
            return false;
        }
        
        // Check advance booking
        if (offer.getMinimumAdvanceBooking() != null && request.getAdvanceBookingDays() != null &&
            request.getAdvanceBookingDays() < offer.getMinimumAdvanceBooking()) {
            return false;
        }
        
        if (offer.getMaximumAdvanceBooking() != null && request.getAdvanceBookingDays() != null &&
            request.getAdvanceBookingDays() > offer.getMaximumAdvanceBooking()) {
            return false;
        }
        
        // Check usage limit
        if (offer.getUsageLimit() != null && offer.getUsageCount() >= offer.getUsageLimit()) {
            return false;
        }
        
        // Check department authorization
        if (request.getDepartment() != null && !offer.getAuthorizedDepartment().equals(request.getDepartment())) {
            return false;
        }
        
        return true;
    }
    
    private OfferEligibilityResponse.EligibleOffer convertToEligibleOffer(Offer offer, OfferEligibilityRequest request) {
        BigDecimal discountAmount = calculateDiscountAmount(offer, request.getBaseAmount());
        BigDecimal finalAmount = request.getBaseAmount().subtract(discountAmount);
        
        return new OfferEligibilityResponse.EligibleOffer(
            offer.getCode(),
            offer.getName(),
            offer.getDescription(),
            discountAmount,
            finalAmount,
            offer.getRequiresApproval(),
            offer.getAuthorizedDepartment(),
            offer.getDiscountType().name(),
            offer.getDiscountValue()
        );
    }
    
    private BigDecimal calculateDiscountAmount(Offer offer, BigDecimal baseAmount) {
        BigDecimal discountAmount = BigDecimal.ZERO;
        
        switch (offer.getDiscountType()) {
            case PERCENTAGE:
                discountAmount = baseAmount.multiply(offer.getDiscountValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                break;
            case FIXED_AMOUNT:
                discountAmount = offer.getDiscountValue();
                break;
            case NIGHTS_FREE:
                // This would require additional logic based on nightly rates
                // For now, treating as fixed amount
                discountAmount = offer.getDiscountValue();
                break;
        }
        
        // Ensure discount doesn't exceed base amount
        if (discountAmount.compareTo(baseAmount) > 0) {
            discountAmount = baseAmount;
        }
        
        return discountAmount.setScale(2, RoundingMode.HALF_UP);
    }
    
    private Offer convertToEntity(OfferDto dto) {
        Offer entity = new Offer();
        updateEntityFromDto(entity, dto);
        return entity;
    }
    
    private void updateEntityFromDto(Offer entity, OfferDto dto) {
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPropertyId(dto.getPropertyId());
        entity.setOfferType(dto.getOfferType());
        entity.setDiscountType(dto.getDiscountType());
        entity.setDiscountValue(dto.getDiscountValue());
        entity.setValidFrom(dto.getValidFrom());
        entity.setValidTo(dto.getValidTo());
        entity.setIsActive(dto.getIsActive());
        entity.setRequiresApproval(dto.getRequiresApproval());
        entity.setAuthorizedDepartment(dto.getAuthorizedDepartment());
        entity.setMinimumAdvanceBooking(dto.getMinimumAdvanceBooking());
        entity.setMaximumAdvanceBooking(dto.getMaximumAdvanceBooking());
        entity.setMinimumStay(dto.getMinimumStay());
        entity.setMaximumStay(dto.getMaximumStay());
        entity.setApplicableRoomTypes(dto.getApplicableRoomTypes());
        entity.setApplicableRatePlans(dto.getApplicableRatePlans());
        entity.setUsageLimit(dto.getUsageLimit());
    }
}