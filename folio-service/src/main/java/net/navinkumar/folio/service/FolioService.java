package net.navinkumar.folio.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.navinkumar.folio.dto.FolioChargeRequest;
import net.navinkumar.folio.dto.FolioRequest;
import net.navinkumar.folio.entity.Folio;
import net.navinkumar.folio.entity.FolioCharge;
import net.navinkumar.folio.repository.FolioChargeRepository;
import net.navinkumar.folio.repository.FolioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FolioService {
    
    private final FolioRepository folioRepository;
    private final FolioChargeRepository folioChargeRepository;
    
    public Folio createFolio(FolioRequest request) {
        log.info("Creating folio for guest {} and reservation {}", request.getGuestId(), request.getReservationId());
        
        Folio folio = new Folio();
        folio.setFolioNumber(generateFolioNumber());
        folio.setGuestId(request.getGuestId());
        folio.setReservationId(request.getReservationId());
        folio.setStatus(Folio.FolioStatus.OPEN);
        folio.setTotalAmount(request.getInitialAmount());
        folio.setBalanceAmount(request.getInitialAmount());
        folio.setCreatedBy("system");
        
        return folioRepository.save(folio);
    }
    
    public FolioCharge addCharge(FolioChargeRequest request) {
        log.info("Adding charge to folio {}", request.getFolioId());
        
        Optional<Folio> folioOpt = folioRepository.findById(request.getFolioId());
        if (folioOpt.isEmpty()) {
            throw new RuntimeException("Folio not found with id: " + request.getFolioId());
        }
        
        Folio folio = folioOpt.get();
        if (folio.getStatus() == Folio.FolioStatus.CLOSED || folio.getStatus() == Folio.FolioStatus.SETTLED) {
            throw new RuntimeException("Cannot add charges to a closed or settled folio");
        }
        
        FolioCharge charge = new FolioCharge();
        charge.setFolio(folio);
        charge.setDescription(request.getDescription());
        charge.setAmount(request.getAmount());
        charge.setChargeType(FolioCharge.ChargeType.valueOf(request.getChargeType()));
        charge.setChargeDate(request.getChargeDate() != null ? request.getChargeDate() : LocalDateTime.now());
        charge.setReference(request.getReference());
        charge.setCreatedBy("system");
        
        FolioCharge savedCharge = folioChargeRepository.save(charge);
        
        // Update folio totals
        updateFolioTotals(folio);
        
        return savedCharge;
    }
    
    @Transactional(readOnly = true)
    public Optional<Folio> getFolioById(Long id) {
        return folioRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Folio> getFolioByNumber(String folioNumber) {
        return folioRepository.findByFolioNumber(folioNumber);
    }
    
    @Transactional(readOnly = true)
    public List<Folio> getFoliosByGuestId(Long guestId) {
        return folioRepository.findByGuestId(guestId);
    }
    
    @Transactional(readOnly = true)
    public List<Folio> getFoliosByReservationId(Long reservationId) {
        return folioRepository.findByReservationId(reservationId);
    }
    
    public Folio closeFolio(Long folioId) {
        log.info("Closing folio {}", folioId);
        
        Optional<Folio> folioOpt = folioRepository.findById(folioId);
        if (folioOpt.isEmpty()) {
            throw new RuntimeException("Folio not found with id: " + folioId);
        }
        
        Folio folio = folioOpt.get();
        folio.setStatus(Folio.FolioStatus.CLOSED);
        folio.setUpdatedBy("system");
        
        return folioRepository.save(folio);
    }
    
    public Folio settleFolio(Long folioId, BigDecimal paymentAmount) {
        log.info("Settling folio {} with payment amount {}", folioId, paymentAmount);
        
        Optional<Folio> folioOpt = folioRepository.findById(folioId);
        if (folioOpt.isEmpty()) {
            throw new RuntimeException("Folio not found with id: " + folioId);
        }
        
        Folio folio = folioOpt.get();
        BigDecimal newPaidAmount = folio.getPaidAmount().add(paymentAmount);
        folio.setPaidAmount(newPaidAmount);
        folio.setBalanceAmount(folio.getTotalAmount().subtract(newPaidAmount));
        
        if (folio.getBalanceAmount().compareTo(BigDecimal.ZERO) <= 0) {
            folio.setStatus(Folio.FolioStatus.SETTLED);
        }
        
        folio.setUpdatedBy("system");
        
        return folioRepository.save(folio);
    }
    
    private void updateFolioTotals(Folio folio) {
        List<FolioCharge> charges = folioChargeRepository.findByFolioId(folio.getId());
        BigDecimal totalAmount = charges.stream()
            .map(FolioCharge::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        folio.setTotalAmount(totalAmount);
        folio.setBalanceAmount(totalAmount.subtract(folio.getPaidAmount()));
        folioRepository.save(folio);
    }
    
    private String generateFolioNumber() {
        return "FOL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}