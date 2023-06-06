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
@Table(name = "USER_GROUP")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserGroup {
    @Id
    private Long id;

    @Column(name = "GROUP_ID")
    private Integer groupId;

    @Column(name = "PARENT_ID")
    private Integer parentId;

    @Column(name = "ACCESS_ID")
    private Integer accessId;

    @Column(name = "PERM_ID")
    private Integer permissionId;

    @Column(name = "DESCRIPTION")
    private String Description;
}
