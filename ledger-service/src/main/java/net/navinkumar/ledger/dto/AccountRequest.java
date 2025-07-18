package net.navinkumar.ledger.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {
    
    @NotNull(message = "Account name is required")
    private String accountName;
    
    @NotNull(message = "Account type is required")
    private String accountType;
    
    private String description;
}