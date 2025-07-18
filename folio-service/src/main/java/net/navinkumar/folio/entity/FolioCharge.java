package net.navinkumar.folio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "folio_charges")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FolioCharge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folio_id", nullable = false)
    private Folio folio;
    
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChargeType chargeType;
    
    @Column(nullable = false)
    private LocalDateTime chargeDate;
    
    @Column
    private String reference;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    private String createdBy;
    
    public enum ChargeType {
        ROOM_CHARGE, TAX, SERVICE_CHARGE, INCIDENTAL, ADDON, DISCOUNT, REFUND
    }
}