package com.beyondctc.app.userapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "COMPANY_PROFILE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProfile {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "COMPANY_NAME")
    private String description;

    @Column(name = "ISPRODUCT_BASED")
    private Boolean isProductBased;
}
