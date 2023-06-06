package com.beyondctc.app.userapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "PERMISSIONS_LOOKUP")
public class PermissionLookup {
    @Id
    private  Long id;

    @Column(name = "PERM_ID")
    private  Integer permissionId;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "SHORT_DESC")
    private String shortDescription;

}
