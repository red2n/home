package net.navinkumar.payment.controller;

import lombok.RequiredArgsConstructor;
import net.navinkumar.payment.dto.PaymentRequest;
import net.navinkumar.payment.dto.RefundRequest;
import net.navinkumar.payment.entity.Payment;
import net.navinkumar.payment.entity.Refund;
import net.navinkumar.payment.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Validated
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping
    public ResponseEntity<Payment> processPayment(@Valid @RequestBody PaymentRequest request) {
        Payment payment = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }
    
    @PostMapping("/refunds")
    public ResponseEntity<Refund> processRefund(@Valid @RequestBody RefundRequest request) {
        Refund refund = paymentService.processRefund(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(refund);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id)
            .map(payment -> ResponseEntity.ok(payment))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/number/{paymentNumber}")
    public ResponseEntity<Payment> getPaymentByNumber(@PathVariable String paymentNumber) {
        return paymentService.getPaymentByNumber(paymentNumber)
            .map(payment -> ResponseEntity.ok(payment))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/folio/{folioId}")
    public ResponseEntity<List<Payment>> getPaymentsByFolioId(@PathVariable Long folioId) {
        List<Payment> payments = paymentService.getPaymentsByFolioId(folioId);
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/{paymentId}/refunds")
    public ResponseEntity<List<Refund>> getRefundsByPaymentId(@PathVariable Long paymentId) {
        List<Refund> refunds = paymentService.getRefundsByPaymentId(paymentId);
        return ResponseEntity.ok(refunds);
    }
}