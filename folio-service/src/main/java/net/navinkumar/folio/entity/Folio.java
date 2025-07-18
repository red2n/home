package net.navinkumar.folio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "folios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Folio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String folioNumber;
    
    @Column(nullable = false)
    private Long guestId;
    
    @Column(nullable = false)
    private Long reservationId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FolioStatus status;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAmount = BigDecimal.ZERO;
    
    @OneToMany(mappedBy = "folio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FolioCharge> charges = new ArrayList<>();
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    private String createdBy;
    
    private String updatedBy;
    
    public enum FolioStatus {
        OPEN, CLOSED, SETTLED
    }
}