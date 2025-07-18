package net.navinkumar.ratemanagement.controller;

import net.navinkumar.ratemanagement.dto.RateCalculationRequest;
import net.navinkumar.ratemanagement.dto.RateCalculationResponse;
import net.navinkumar.ratemanagement.dto.RatePlanDto;
import net.navinkumar.ratemanagement.entity.RatePlan;
import net.navinkumar.ratemanagement.service.RateManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rate-management")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RateManagementController {
    
    private final RateManagementService rateManagementService;
    
    @PostMapping("/rate-plans")
    public ResponseEntity<RatePlan> createRatePlan(
            @Valid @RequestBody RatePlanDto ratePlanDto,
            @RequestHeader(value = "X-User-ID", defaultValue = "system") String userId) {
        
        log.info("Request to create rate plan: {}", ratePlanDto.getCode());
        
        try {
            RatePlan createdRatePlan = rateManagementService.createRatePlan(ratePlanDto, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRatePlan);
        } catch (IllegalArgumentException e) {
            log.error("Error creating rate plan: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/rate-plans/{id}")
    public ResponseEntity<RatePlan> updateRatePlan(
            @PathVariable Long id,
            @Valid @RequestBody RatePlanDto ratePlanDto,
            @RequestHeader(value = "X-User-ID", defaultValue = "system") String userId) {
        
        log.info("Request to update rate plan with id: {}", id);
        
        try {
            RatePlan updatedRatePlan = rateManagementService.updateRatePlan(id, ratePlanDto, userId);
            return ResponseEntity.ok(updatedRatePlan);
        } catch (IllegalArgumentException e) {
            log.error("Error updating rate plan: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/rate-plans/property/{propertyId}")
    public ResponseEntity<List<RatePlan>> getRatePlansForProperty(@PathVariable String propertyId) {
        log.info("Request to get rate plans for property: {}", propertyId);
        
        List<RatePlan> ratePlans = rateManagementService.getActiveRatePlansForProperty(propertyId);
        return ResponseEntity.ok(ratePlans);
    }
    
    @PostMapping("/rates/calculate")
    public ResponseEntity<RateCalculationResponse> calculateRate(@Valid @RequestBody RateCalculationRequest request) {
        log.info("Request to calculate rate for property: {}, room type: {}", 
                request.getPropertyId(), request.getRoomType());
        
        try {
            RateCalculationResponse response = rateManagementService.calculateRate(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Error calculating rate: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Rate Management Service is running");
    }
}