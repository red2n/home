package net.navinkumar.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String paymentNumber;
    
    @Column(nullable = false)
    private Long folioId;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;
    
    @Column
    private String cardToken; // For PCI compliance
    
    @Column
    private String cardLast4;
    
    @Column
    private String cardBrand;
    
    @Column
    private String gatewayTransactionId;
    
    @Column
    private String gatewayResponse;
    
    @Column
    private String reference;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    private String createdBy;
    
    private String updatedBy;
    
    public enum PaymentMethod {
        CREDIT_CARD, DEBIT_CARD, CASH, BANK_TRANSFER, CHECK, DIGITAL_WALLET
    }
    
    public enum PaymentStatus {
        PENDING, AUTHORIZED, CAPTURED, FAILED, CANCELLED, REFUNDED, PARTIALLY_REFUNDED
    }
}