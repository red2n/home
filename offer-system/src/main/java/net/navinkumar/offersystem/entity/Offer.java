package net.navinkumar.offersystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "offers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Offer {
    
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
    
    @Enumerated(EnumType.STRING)
    @Column(name = "offer_type", nullable = false)
    private OfferType offerType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;
    
    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;
    
    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;
    
    @Column(name = "valid_to")
    private LocalDate validTo;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "requires_approval")
    private Boolean requiresApproval = false;
    
    @Column(name = "authorized_department", nullable = false)
    private String authorizedDepartment;
    
    @Column(name = "minimum_advance_booking")
    private Integer minimumAdvanceBooking;
    
    @Column(name = "maximum_advance_booking")
    private Integer maximumAdvanceBooking;
    
    @Column(name = "minimum_stay")
    private Integer minimumStay;
    
    @Column(name = "maximum_stay")
    private Integer maximumStay;
    
    @Column(name = "applicable_room_types")
    private String applicableRoomTypes; // Comma-separated list
    
    @Column(name = "applicable_rate_plans")
    private String applicableRatePlans; // Comma-separated list
    
    @Column(name = "usage_limit")
    private Integer usageLimit;
    
    @Column(name = "usage_count")
    private Integer usageCount = 0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "updated_by")
    private String updatedBy;
    
    public enum OfferType {
        ROOM_DISCOUNT, PACKAGE_DEAL, SPECIAL_RATE, LOYALTY_REWARD, PROMOTIONAL
    }
    
    public enum DiscountType {
        PERCENTAGE, FIXED_AMOUNT, NIGHTS_FREE
    }
    
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