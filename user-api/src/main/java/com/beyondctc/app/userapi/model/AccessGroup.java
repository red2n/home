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
@Table(name = "ACCESS_GROUP")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccessGroup {

    @Id
    private Long id;

    @Column(name = "ACCESS_ID")
    private Integer accessID;

    @Column(name = "PARENT_ID")
    private Integer parentId;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ROUTES")
    private String routes;

    @Column(name = "MAP_ROUTE")
    private String mapRoutes;
}
