package net.navinkumar.payment.repository;

import net.navinkumar.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByPaymentNumber(String paymentNumber);
    
    List<Payment> findByFolioId(Long folioId);
    
    List<Payment> findByStatus(Payment.PaymentStatus status);
    
    Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId);
}