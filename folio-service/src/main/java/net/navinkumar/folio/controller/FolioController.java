package net.navinkumar.folio.controller;

import lombok.RequiredArgsConstructor;
import net.navinkumar.folio.dto.FolioChargeRequest;
import net.navinkumar.folio.dto.FolioRequest;
import net.navinkumar.folio.entity.Folio;
import net.navinkumar.folio.entity.FolioCharge;
import net.navinkumar.folio.service.FolioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/folios")
@RequiredArgsConstructor
@Validated
public class FolioController {
    
    private final FolioService folioService;
    
    @PostMapping
    public ResponseEntity<Folio> createFolio(@Valid @RequestBody FolioRequest request) {
        Folio folio = folioService.createFolio(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(folio);
    }
    
    @PostMapping("/charges")
    public ResponseEntity<FolioCharge> addCharge(@Valid @RequestBody FolioChargeRequest request) {
        FolioCharge charge = folioService.addCharge(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(charge);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Folio> getFolioById(@PathVariable Long id) {
        return folioService.getFolioById(id)
            .map(folio -> ResponseEntity.ok(folio))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/number/{folioNumber}")
    public ResponseEntity<Folio> getFolioByNumber(@PathVariable String folioNumber) {
        return folioService.getFolioByNumber(folioNumber)
            .map(folio -> ResponseEntity.ok(folio))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/guest/{guestId}")
    public ResponseEntity<List<Folio>> getFoliosByGuestId(@PathVariable Long guestId) {
        List<Folio> folios = folioService.getFoliosByGuestId(guestId);
        return ResponseEntity.ok(folios);
    }
    
    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<List<Folio>> getFoliosByReservationId(@PathVariable Long reservationId) {
        List<Folio> folios = folioService.getFoliosByReservationId(reservationId);
        return ResponseEntity.ok(folios);
    }
    
    @PutMapping("/{id}/close")
    public ResponseEntity<Folio> closeFolio(@PathVariable Long id) {
        Folio folio = folioService.closeFolio(id);
        return ResponseEntity.ok(folio);
    }
    
    @PutMapping("/{id}/settle")
    public ResponseEntity<Folio> settleFolio(@PathVariable Long id, @RequestParam BigDecimal paymentAmount) {
        Folio folio = folioService.settleFolio(id, paymentAmount);
        return ResponseEntity.ok(folio);
    }
}