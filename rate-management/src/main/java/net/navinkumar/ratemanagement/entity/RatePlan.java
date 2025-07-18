package net.navinkumar.ratemanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rate_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatePlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String code;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "property_id", nullable = false)
    private String propertyId;
    
    @Column(name = "room_type", nullable = false)
    private String roomType;
    
    @Column(name = "base_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseRate;
    
    @Column(name = "currency_code", nullable = false)
    private String currencyCode;
    
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;
    
    @Column(name = "valid_to")
    private LocalDate validTo;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "is_refundable")
    private Boolean isRefundable = true;
    
    @Column(name = "advance_booking_days")
    private Integer advanceBookingDays;
    
    @Column(name = "minimum_stay")
    private Integer minimumStay = 1;
    
    @Column(name = "maximum_stay")
    private Integer maximumStay;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "updated_by")
    private String updatedBy;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}