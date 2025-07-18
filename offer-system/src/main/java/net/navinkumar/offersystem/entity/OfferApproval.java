package net.navinkumar.offersystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "offer_approvals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferApproval {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", nullable = false)
    private Offer offer;
    
    @Column(name = "reservation_id")
    private String reservationId;
    
    @Column(name = "guest_id")
    private String guestId;
    
    @Column(name = "requested_by", nullable = false)
    private String requestedBy;
    
    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApprovalStatus status = ApprovalStatus.PENDING;
    
    @Column(name = "approved_by")
    private String approvedBy;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(columnDefinition = "TEXT")
    private String comments;
    
    @Column(name = "rejection_reason")
    private String rejectionReason;
    
    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED, EXPIRED
    }
    
    @PrePersist
    protected void onCreate() {
        requestedAt = LocalDateTime.now();
    }
}