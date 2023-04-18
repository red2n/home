package tech.iamwith.usergateway.models;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "USR_WORK_PROFILE")
@Getter
@Setter
@NoArgsConstructor
public class UserProfile {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "USR_ID")
    private Long usrId;

    @Column(name = "C_ID")
    private Long company;

    @Column(name = "DURATION_FROM")
    private Date durationFrom;

    @Column(name = "DURATION_TO")
    private Date durationTo;

    @Column(name = "CURRENT_CTC")
    private  int currentCtc;

    @Column(name = "EXPECTED_CTC")
    private  int expectedCtc;

    @Column(name = "UPDATED_DATE_TIME")
    private Date upd_dateTime;
}