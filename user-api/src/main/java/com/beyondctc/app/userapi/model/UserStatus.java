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
@Table(name = "USERSTATUS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatus {

    @Id
    @Column(name = "STATUS_ID")
    private Integer id;

    @Column(name = "DESCRIPTION")
    private String description;
}
