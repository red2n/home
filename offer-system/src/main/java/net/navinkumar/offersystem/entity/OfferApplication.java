package net.navinkumar.offersystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "offer_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferApplication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", nullable = false)
    private Offer offer;
    
    @Column(name = "reservation_id", nullable = false)
    private String reservationId;
    
    @Column(name = "guest_id")
    private String guestId;
    
    @Column(name = "original_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal originalAmount;
    
    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount;
    
    @Column(name = "final_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalAmount;
    
    @Column(name = "applied_by", nullable = false)
    private String appliedBy;
    
    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt;
    
    @Column(name = "approval_id")
    private Long approvalId;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @PrePersist
    protected void onCreate() {
        appliedAt = LocalDateTime.now();
    }
}