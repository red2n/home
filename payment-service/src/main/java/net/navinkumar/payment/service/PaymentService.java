package net.navinkumar.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.navinkumar.payment.dto.PaymentRequest;
import net.navinkumar.payment.dto.RefundRequest;
import net.navinkumar.payment.entity.Payment;
import net.navinkumar.payment.entity.Refund;
import net.navinkumar.payment.repository.PaymentRepository;
import net.navinkumar.payment.repository.RefundRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    
    public Payment processPayment(PaymentRequest request) {
        log.info("Processing payment for folio {} with amount {}", request.getFolioId(), request.getAmount());
        
        Payment payment = new Payment();
        payment.setPaymentNumber(generatePaymentNumber());
        payment.setFolioId(request.getFolioId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(Payment.PaymentMethod.valueOf(request.getPaymentMethod()));
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setCardToken(request.getCardToken());
        payment.setCardLast4(request.getCardLast4());
        payment.setCardBrand(request.getCardBrand());
        payment.setReference(request.getReference());
        payment.setCreatedBy("system");
        
        // Simulate payment gateway processing
        if (simulatePaymentProcessing(payment)) {
            payment.setStatus(Payment.PaymentStatus.CAPTURED);
            payment.setGatewayTransactionId(UUID.randomUUID().toString());
            payment.setGatewayResponse("SUCCESS");
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setGatewayResponse("PAYMENT_FAILED");
        }
        
        return paymentRepository.save(payment);
    }
    
    public Refund processRefund(RefundRequest request) {
        log.info("Processing refund for payment {} with amount {}", request.getPaymentId(), request.getAmount());
        
        Optional<Payment> paymentOpt = paymentRepository.findById(request.getPaymentId());
        if (paymentOpt.isEmpty()) {
            throw new RuntimeException("Payment not found with id: " + request.getPaymentId());
        }
        
        Payment payment = paymentOpt.get();
        if (payment.getStatus() != Payment.PaymentStatus.CAPTURED) {
            throw new RuntimeException("Cannot refund payment that is not captured");
        }
        
        // Check if refund amount doesn't exceed payment amount
        BigDecimal totalRefunded = getTotalRefundedAmount(payment.getId());
        BigDecimal availableForRefund = payment.getAmount().subtract(totalRefunded);
        
        if (request.getAmount().compareTo(availableForRefund) > 0) {
            throw new RuntimeException("Refund amount exceeds available refund amount");
        }
        
        Refund refund = new Refund();
        refund.setRefundNumber(generateRefundNumber());
        refund.setPayment(payment);
        refund.setAmount(request.getAmount());
        refund.setReason(request.getReason());
        refund.setStatus(Refund.RefundStatus.PENDING);
        refund.setCreatedBy("system");
        
        // Simulate refund processing
        if (simulateRefundProcessing(refund)) {
            refund.setStatus(Refund.RefundStatus.PROCESSED);
            refund.setGatewayRefundId(UUID.randomUUID().toString());
            refund.setGatewayResponse("REFUND_SUCCESS");
            
            // Update payment status if fully refunded
            BigDecimal newTotalRefunded = totalRefunded.add(request.getAmount());
            if (newTotalRefunded.compareTo(payment.getAmount()) == 0) {
                payment.setStatus(Payment.PaymentStatus.REFUNDED);
            } else if (newTotalRefunded.compareTo(BigDecimal.ZERO) > 0) {
                payment.setStatus(Payment.PaymentStatus.PARTIALLY_REFUNDED);
            }
            paymentRepository.save(payment);
        } else {
            refund.setStatus(Refund.RefundStatus.FAILED);
            refund.setGatewayResponse("REFUND_FAILED");
        }
        
        return refundRepository.save(refund);
    }
    
    @Transactional(readOnly = true)
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Payment> getPaymentByNumber(String paymentNumber) {
        return paymentRepository.findByPaymentNumber(paymentNumber);
    }
    
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByFolioId(Long folioId) {
        return paymentRepository.findByFolioId(folioId);
    }
    
    @Transactional(readOnly = true)
    public List<Refund> getRefundsByPaymentId(Long paymentId) {
        return refundRepository.findByPaymentId(paymentId);
    }
    
    private BigDecimal getTotalRefundedAmount(Long paymentId) {
        List<Refund> refunds = refundRepository.findByPaymentId(paymentId);
        return refunds.stream()
            .filter(refund -> refund.getStatus() == Refund.RefundStatus.PROCESSED)
            .map(Refund::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private boolean simulatePaymentProcessing(Payment payment) {
        // Simple simulation - fail if amount is exactly 100.00 for testing
        return !payment.getAmount().equals(new BigDecimal("100.00"));
    }
    
    private boolean simulateRefundProcessing(Refund refund) {
        // Simple simulation - always succeed
        return true;
    }
    
    private String generatePaymentNumber() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private String generateRefundNumber() {
        return "REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}