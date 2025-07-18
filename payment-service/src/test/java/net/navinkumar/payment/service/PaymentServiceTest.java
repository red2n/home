package net.navinkumar.payment.service;

import net.navinkumar.payment.dto.PaymentRequest;
import net.navinkumar.payment.entity.Payment;
import net.navinkumar.payment.repository.PaymentRepository;
import net.navinkumar.payment.repository.RefundRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RefundRepository refundRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void processPayment_ShouldReturnProcessedPayment() {
        // Given
        PaymentRequest request = new PaymentRequest();
        request.setFolioId(1L);
        request.setAmount(new BigDecimal("50.00"));
        request.setPaymentMethod("CREDIT_CARD");
        request.setCardLast4("1234");

        Payment savedPayment = new Payment();
        savedPayment.setId(1L);
        savedPayment.setFolioId(1L);
        savedPayment.setAmount(new BigDecimal("50.00"));
        savedPayment.setStatus(Payment.PaymentStatus.CAPTURED);

        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        // When
        Payment result = paymentService.processPayment(request);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getFolioId());
        assertEquals(new BigDecimal("50.00"), result.getAmount());
        assertEquals(Payment.PaymentStatus.CAPTURED, result.getStatus());
    }

    @Test
    void processPayment_WithFailureAmount_ShouldReturnFailedPayment() {
        // Given
        PaymentRequest request = new PaymentRequest();
        request.setFolioId(1L);
        request.setAmount(new BigDecimal("100.00")); // This amount triggers failure in simulation
        request.setPaymentMethod("CREDIT_CARD");

        Payment savedPayment = new Payment();
        savedPayment.setId(1L);
        savedPayment.setFolioId(1L);
        savedPayment.setAmount(new BigDecimal("100.00"));
        savedPayment.setStatus(Payment.PaymentStatus.FAILED);

        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        // When
        Payment result = paymentService.processPayment(request);

        // Then
        assertNotNull(result);
        assertEquals(Payment.PaymentStatus.FAILED, result.getStatus());
    }
}