package tech.iamwith.usergateway.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "USERS")
@Getter
@Setter
@NoArgsConstructor
public class Users {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "ROLE_ID")
    private int role;

    @Column(name = "EMAIL")
    private String email;

    @Transient
    private String emailUrl;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "USR_PASS")
    private String password;

    @Column(name = "UPDATED_DATE_TIME")
    private Date upd_dateTime;

    @Column(name = "USER_STATUS")
    private int status;

}
