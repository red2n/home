package net.navinkumar.payment.repository;

import net.navinkumar.payment.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
    
    Optional<Refund> findByRefundNumber(String refundNumber);
    
    List<Refund> findByPaymentId(Long paymentId);
    
    List<Refund> findByStatus(Refund.RefundStatus status);
}